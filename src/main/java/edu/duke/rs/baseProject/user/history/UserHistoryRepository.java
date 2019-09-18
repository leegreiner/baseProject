package edu.duke.rs.baseProject.user.history;

import java.util.List;

public interface UserHistoryRepository {
  List<UserHistory> listUserRevisions(Long userId);
}
