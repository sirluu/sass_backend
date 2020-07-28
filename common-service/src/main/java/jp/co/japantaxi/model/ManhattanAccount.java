package jp.co.japantaxi.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ManhattanAccount {
  private String accountId;
  private String email;
  private String firstName;
  private String lastName;
  private String locale;
  private List<Organization> organizations;
  private Organization organization;
  private List<PermissionGroups> permissionGroups;
  
  @Getter
  @Setter
  public static class Organization {
    private String organizationId;
    private String name;
    private String organizationType;
  }
  
  @Getter
  @Setter
  public static class PermissionGroups {
    private String permissionGroupId;
    private String permissionGroupName;
    private List<Permission> permissions;
  }
  
  @Getter
  @Setter
  public static class Permission {
    private Integer permissionId;
    private String permissionName;
    private Boolean create;
    private Boolean read;
    private Boolean update;
    private Boolean delete;
  }
}
