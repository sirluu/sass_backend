import logging
from typing import Any, Dict, List, Optional

from flask import current_app
from pinecone import Pinecone
from sentence_transformers import SentenceTransformer

logger = logging.getLogger(__name__)


class VectorService:
    """Service for managing vector embeddings and similarity search with Pinecone."""

    def __init__(self):
        self.model = None
        self.index = None
        self.initialized = False

    def initialize(self):
        try:
            self.pc = Pinecone(api_key=current_app.config["PINECONE_API_KEY"])
            index_name = current_app.config["PINECONE_INDEX_NAME"]
            self.index = self.pc.Index(index_name)
            self.model = SentenceTransformer(current_app.config["EMBEDDING_MODEL"])
            self.initialized = True
            logger.info("Vector service initialized successfully")
        except Exception as e:
            logger.error(f"Failed to initialize vector service: {str(e)}")
            raise

    def generate_embedding(self, text: str, *, is_query: bool = False) -> List[float]:
        if not self.initialized:
            self.initialize()

        try:
            model_name = current_app.config.get("EMBEDDING_MODEL", "")
            if "e5" in model_name.lower():
                prefix = "query: " if is_query else "passage: "
                text = prefix + text

            embedding = self.model.encode(text)
            return embedding.tolist()
        except Exception as e:
            logger.error(f"Failed to generate embedding: {str(e)}")
            raise

    def upsert_product_embedding(
        self,
        product_id: str,
        text: str,
        metadata: Dict[str, Any] = None,
        namespace: Optional[str] = None,
    ):
        if not self.initialized:
            self.initialize()

        try:
            embedding = self.generate_embedding(text)
            vector_data = {
                "id": product_id,
                "values": embedding,
                "metadata": metadata or {},
            }
            self.index.upsert(vectors=[vector_data], namespace=namespace or "")
            logger.info(
                f"Upserted embedding for product: {product_id} (ns: {namespace or 'default'})"
            )
        except Exception as e:
            logger.error(f"Failed to upsert product embedding: {str(e)}")
            raise

    def search_similar_products(
        self,
        query_text: str,
        top_k: int = 10,
        filter_dict: Dict[str, Any] = None,
        namespace: Optional[str] = None,
    ) -> List[Dict[str, Any]]:
        if not self.initialized:
            self.initialize()

        try:
            query_embedding = self.generate_embedding(query_text, is_query=True)
            search_kwargs = {
                "vector": query_embedding,
                "top_k": top_k,
                "include_metadata": True,
                "include_values": False,
                "namespace": namespace or "",
            }
            if filter_dict:
                search_kwargs["filter"] = filter_dict

            results = self.index.query(**search_kwargs)
            similar_products = []
            for match in results["matches"]:
                similar_products.append(
                    {
                        "id": match["id"],
                        "score": match["score"],
                        "metadata": match.get("metadata", {}),
                    }
                )
            return similar_products
        except Exception as e:
            logger.error(f"Failed to search similar products: {str(e)}")
            return []

    def delete_product_embedding(
        self, product_id: str, namespace: Optional[str] = None
    ):
        if not self.initialized:
            self.initialize()

        try:
            self.index.delete(ids=[product_id], namespace=namespace or "")
            logger.info(f"Deleted embedding for product: {product_id}")
        except Exception as e:
            logger.error(f"Failed to delete product embedding: {str(e)}")
            raise

    def get_index_stats(self) -> Dict[str, Any]:
        if not self.initialized:
            self.initialize()

        try:
            return self.index.describe_index_stats()
        except Exception as e:
            logger.error(f"Failed to get index stats: {str(e)}")
            return {}

    def batch_upsert_products(
        self,
        products: List[Dict[str, Any]],
        batch_size: int = 100,
        namespace: Optional[str] = None,
    ):
        if not self.initialized:
            self.initialize()

        try:
            vectors = []
            for product in products:
                embedding = self.generate_embedding(product["text"])
                vectors.append(
                    {
                        "id": product["id"],
                        "values": embedding,
                        "metadata": product.get("metadata", {}),
                    }
                )
                if len(vectors) >= batch_size:
                    self.index.upsert(vectors=vectors, namespace=namespace or "")
                    vectors = []
            if vectors:
                self.index.upsert(vectors=vectors, namespace=namespace or "")
            logger.info(
                f"Batch upserted {len(products)} embeddings (ns: {namespace or 'default'})"
            )
        except Exception as e:
            logger.error(f"Failed to batch upsert products: {str(e)}")
            raise
