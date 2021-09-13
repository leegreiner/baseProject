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
import javax.persistence.ManyToMany;
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
@Table(name = "privilege",
  indexes = {
      @Index(name = "UIX_PRIVILEGE_ALT_ID", unique = true, columnList="alternate_id")
  }
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Audited
public class Privilege extends BaseEntity implements Serializable {
  private static final long serialVersionUID = 1L;
  
  @Column(name = "privilege_id")
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column(length = 30)
  @NonNull
  @Convert(converter = PrivilegeName.Converter.class)
  private PrivilegeName name;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "privileges")
  @ToString.Exclude
  private Set<Role> roles;
}
