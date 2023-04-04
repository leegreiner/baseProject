package edu.duke.rs.baseProject.user;

import java.io.Serializable;

import org.hibernate.envers.Audited;

import edu.duke.rs.baseProject.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "password_history",
  uniqueConstraints = {
    @UniqueConstraint(name="UC_PWD_HIST_USER_PWD", columnNames = {"user_fk", "password"})  
  },
  indexes = {
    @Index(name = "IX_PWD_HIST_USER_FK", unique = false, columnList="user_fk")
  }
)
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
