package edu.duke.rs.baseProject.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.duke.rs.baseProject.model.BaseEntity;
import edu.duke.rs.baseProject.role.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Entity
@Audited
@Table(name = "users",
  indexes = {
      @Index(name = "UIX_USER_ALT_ID", unique = true, columnList="alternate_id"),
      @Index(name = "IX_USER_LAST_LOGGED_IN", unique = false, columnList="last_logged_in"),
      @Index(name = "IX_USER_LAST_PWD_CHG_ID", unique = false, columnList="password_chg_id_create_time")
  }
)
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = UserConstants.USER_ROLES_AND_PRIVILEGES_ENTITY_GRAPH, 
      attributeNodes = @NamedAttributeNode(value = "roles", subgraph = "subgraph.roles"), 
      subgraphs = {
          @NamedSubgraph(name = "subgraph.roles", 
              attributeNodes = {
                  @NamedAttributeNode(value = "privileges")
              }
          )
      }),
  @NamedEntityGraph(
      name = UserConstants.USER_AND_ROLES_ENTITY_GRAPH, 
      attributeNodes = @NamedAttributeNode("roles")
  )
})
@NamedQueries({
  @NamedQuery(
      name = "User.expirePasswordChangeIds",
      query = "update User user set user.passwordChangeId = null, user.passwordChangeIdCreationTime = null where user.passwordChangeIdCreationTime < :time"),
  @NamedQuery(
      name = "User.disableUnusedAccounts",
      query = "update User user set user.accountEnabled = false where user.accountEnabled = true and user.lastLoggedIn < :date")
  
})
public class User extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id", updatable = false, nullable = false)
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 30, unique = true, nullable = false)
	@NonNull
	@NotBlank
	@Size(min = 4, max = 30)
	private String username;
	
	@Column(length = 200, nullable = false)
	@NonNull
	@NotBlank
	@Size(min = 8, max = 200)
	private String password;
	
	@Column(length = 30, nullable = false)
	@NonNull
	@NotBlank
	@Size(max = 30)
	private String firstName;
	
	@Column(length = 1)
  @Size(max = 1)
	private String middleInitial;
	
	@Column(length = 30, nullable = false)
  @NonNull
  @NotBlank
  @Size(max = 30)
	private String lastName;
	
	@Column(length = 320, unique = true, nullable = false)
  @NonNull
  @NotBlank
  @Email
  private String email;
	
	@Column(nullable = false)
	@Convert(converter = org.hibernate.type.TrueFalseConverter.class)
	private boolean accountEnabled = true;
	
	@Column(length = 100, nullable = false)
	@NotNull
	private TimeZone timeZone = TimeZone.getTimeZone("UTC");
	
	@Column(name = "last_logged_in", nullable = true)
	private LocalDateTime lastLoggedIn;
	
	private LocalDateTime lastInvalidLoginAttempt;
	
	private Integer invalidLoginAttempts;
	
  private LocalDateTime lastPasswordChange;
	
  @Column(name = "password_change_id", length = 16, nullable = true, unique = true)
	private UUID passwordChangeId;
	
	@Column(name = "password_chg_id_create_time", nullable = true)
	private LocalDateTime passwordChangeIdCreationTime;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "users_to_roles",
		joinColumns = @JoinColumn(name = "user_fk", referencedColumnName = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_fk", referencedColumnName = "role_id")
	)
	@NonNull
	@NotEmpty
	@ToString.Exclude
	private Set<Role> roles;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonIgnore
  @ToString.Exclude
	private List<PasswordHistory> passwordHistory = new ArrayList<PasswordHistory>();
	
	public String getDisplayName() {
	  final StringBuffer buf = new StringBuffer();
	  
	  if (StringUtils.hasLength(firstName)) {
	    buf.append(firstName + " ");
	  }
	  
	  if (StringUtils.hasLength(middleInitial)) {
      buf.append(middleInitial + " ");
    }
	  
	  if (StringUtils.hasLength(lastName)) {
      buf.append(lastName);
    }
	  
	  return buf.toString();
	}
	
	public void addPasswordHistory(final PasswordHistory passwordHistory) {
	  this.getPasswordHistory().add(passwordHistory);
	  passwordHistory.setUser(this);
	}
	
	public void removePasswordHistory(final PasswordHistory passwordHistory) {
	  this.getPasswordHistory().remove(passwordHistory);
	  passwordHistory.setUser(null);
	}
}
