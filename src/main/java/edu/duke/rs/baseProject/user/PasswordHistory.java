package edu.duke.rs.baseProject.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import edu.duke.rs.baseProject.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Entity
@Audited
@Table(name = "password_history",
  indexes = {
      @Index(name = "IX_PASSWORD_HISTORY_PWD", unique = true, columnList="password")
})
public class PasswordHistory extends BaseEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "password_history_id")
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passwordHistory_seq")
  @SequenceGenerator(name = "passwordHistory_seq", sequenceName = "PASSWORD_HISTORY_SEQ", allocationSize = 1)
  private Long id;
  
  @Column(name = "password", length = 200, nullable = false)
  @NonNull
  @NotBlank
  @Size(min = 8, max = 200)
  private String password;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_fk")
  @ToString.Exclude
  private User user;
}
