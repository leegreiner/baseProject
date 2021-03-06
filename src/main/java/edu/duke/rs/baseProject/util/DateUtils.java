package edu.duke.rs.baseProject.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.duke.rs.baseProject.security.SecurityUtils;

public class DateUtils {
  public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
  public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter
      .ofPattern(DEFAULT_DATE_TIME_FORMAT);
  private final SecurityUtils securityUtils;

  public DateUtils(final SecurityUtils securityUtils) {
    this.securityUtils = securityUtils;
  }

  public DateUtils() {
    this(new SecurityUtils());
  }

  public void convertLocalDateTimeToCurrentUserTime(final Object object) {
    final ZoneId currentUserZoneId = getCurrentUserZoneId();

    if (currentUserZoneId == null) {
      return;
    }

    final Class<?> aClass = object.getClass();
    final List<Field> localDateTimeFields = getFieldsByType(aClass, LocalDateTime.class);

    localDateTimeFields.stream().forEach(field -> {
      try {
        final PropertyDescriptor pd = new PropertyDescriptor(field.getName(), aClass);
        final Method getter = pd.getReadMethod();
        final Method setter = pd.getWriteMethod();

        if (getter != null && setter != null) {
          setter.invoke(object,
              convertToZone((LocalDateTime) getter.invoke(object), ZoneId.systemDefault(), currentUserZoneId));
        }
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
          | IntrospectionException e) {
        ;
        ;
      }
    });
  }

  public void convertCurrentUserTimeToLocalDateTime(final Object object) {
    final ZoneId currentUserZoneId = getCurrentUserZoneId();

    if (currentUserZoneId == null) {
      return;
    }

    final Class<?> aClass = object.getClass();
    final List<Field> localDateTimeFields = getFieldsByType(aClass, LocalDateTime.class);

    localDateTimeFields.stream().forEach(field -> {
      try {
        final PropertyDescriptor pd = new PropertyDescriptor(field.getName(), aClass);
        final Method getter = pd.getReadMethod();
        final Method setter = pd.getWriteMethod();

        if (getter != null && setter != null) {
          setter.invoke(object,
              convertToZone((LocalDateTime) getter.invoke(object), currentUserZoneId, ZoneId.systemDefault()));
        }
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
          | IntrospectionException e) {
        ;
        ;
      }
    });
  }

  public LocalDateTime convertToZone(final LocalDateTime dateTime, final ZoneId fromZoneId, final ZoneId toZoneId) {
    final ZonedDateTime fromZonedDateTime = ZonedDateTime.of(dateTime, fromZoneId);
    final ZonedDateTime toZonedDateTime = fromZonedDateTime.withZoneSameInstant(toZoneId);

    return toZonedDateTime.toLocalDateTime();
  }

  private static List<Field> getFieldsByType(final Class<?> clazz, final Class<?> type) {
    return Arrays.asList(clazz.getDeclaredFields()).stream().filter(field -> field.getType().equals(type))
        .collect(Collectors.toList());
  }

  private ZoneId getCurrentUserZoneId() {
    return securityUtils.getPrincipal().map(appPrincipal -> appPrincipal.getTimeZone().toZoneId())
        .orElseGet(() -> null);
  }
}
