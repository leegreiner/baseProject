package edu.duke.rs.baseProject.role;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

import edu.duke.rs.baseProject.model.BaseEntity;
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
@Table(name = "role",
  indexes = {
      @Index(name = "UIX_ROLE_ALT_ID", unique = true, columnList="alternate_id")
  }
)
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = RoleConstants.ROLE_AND_PRIVILEGES_ENTITY_GRAPH, 
      attributeNodes = @NamedAttributeNode(value = "privileges"))
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Audited
public class Role extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "role_id", updatable = false, nullable = false)
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 30, updatable = false, unique = true, nullable = false)
	@NonNull
	@Convert(converter = RoleName.Converter.class)
	private RoleName name;
	
	@ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "roles_to_privileges", 
    joinColumns = @JoinColumn(name = "role_fk", referencedColumnName = "role_id"), 
    inverseJoinColumns = @JoinColumn(name = "privilege_fk", referencedColumnName = "privilege_id"))
	@ToString.Exclude
	private Set<Privilege> privileges;
}
