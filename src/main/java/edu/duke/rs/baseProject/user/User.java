package edu.duke.rs.baseProject.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.duke.rs.baseProject.model.ESignedBaseEntity;
import edu.duke.rs.baseProject.role.Role;
import io.micrometer.core.instrument.util.StringUtils;
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
      //@Index(name = "UIX_USER_USERNAME", unique = true, columnList="username"),
      //@Index(name = "UIX_USER_EMAIL", unique = true, columnList="email"),
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
public class User extends ESignedBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id")
	@Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
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
	
	@Column(length = 64, nullable = false, updatable = false)
	@NotBlank
	private String secret;
	
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
  @Size(max = 320)
  private String email;
	
	@Column(nullable = false)
	@Type(type = "yes_no")
	private boolean accountEnabled = true;
	
	@Column(length = 100, nullable = false)
	@NotNull
	private TimeZone timeZone = TimeZone.getTimeZone("UTC");
	
	@Column(name = "last_logged_in", nullable = true)
	private LocalDateTime lastLoggedIn;
	
	private LocalDateTime lastInvalidLoginAttempt;
	
	private Integer invalidLoginAttempts;
	
  private LocalDateTime lastPasswordChange;
	
	@Column(length = 36, nullable = true)
	@Type(type = "uuid-char")
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
	  
	  if (StringUtils.isNotBlank(firstName)) {
	    buf.append(firstName + " ");
	  }
	  
	  if (StringUtils.isNotBlank(middleInitial)) {
      buf.append(middleInitial + " ");
    }
	  
	  if (StringUtils.isNotBlank(lastName)) {
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
