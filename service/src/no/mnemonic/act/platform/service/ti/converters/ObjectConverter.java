package no.mnemonic.act.platform.service.ti.converters;

import no.mnemonic.act.platform.api.model.v1.FactType;
import no.mnemonic.act.platform.api.model.v1.Object;
import no.mnemonic.act.platform.api.model.v1.ObjectFactsStatistic;
import no.mnemonic.act.platform.api.model.v1.ObjectType;
import no.mnemonic.act.platform.dao.api.result.ObjectStatisticsContainer;
import no.mnemonic.act.platform.dao.cassandra.entity.ObjectEntity;
import no.mnemonic.commons.utilities.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectConverter implements Converter<ObjectEntity, Object> {

  private final Function<UUID, ObjectType> objectTypeConverter;
  private final Function<UUID, FactType> factTypeConverter;
  private final Function<UUID, Collection<ObjectStatisticsContainer.FactStatistic>> factStatisticsResolver;

  @Inject
  public ObjectConverter(Function<UUID, ObjectType> objectTypeConverter,
                         Function<UUID, FactType> factTypeConverter,
                         Function<UUID, Collection<ObjectStatisticsContainer.FactStatistic>> factStatisticsResolver) {
    this.objectTypeConverter = objectTypeConverter;
    this.factTypeConverter = factTypeConverter;
    this.factStatisticsResolver = factStatisticsResolver;
  }

  @Override
  public Class<ObjectEntity> getSourceType() {
    return ObjectEntity.class;
  }

  @Override
  public Class<Object> getTargetType() {
    return Object.class;
  }

  @Override
  public Object apply(ObjectEntity entity) {
    if (entity == null) return null;
    return Object.builder()
            .setId(entity.getId())
            .setType(objectTypeConverter.apply(entity.getTypeID()).toInfo())
            .setValue(entity.getValue())
            .setStatistics(resolveStatistics(entity))
            .build();
  }

  private List<ObjectFactsStatistic> resolveStatistics(ObjectEntity entity) {
    Collection<ObjectStatisticsContainer.FactStatistic> statistics = factStatisticsResolver.apply(entity.getId());
    if (CollectionUtils.isEmpty(statistics)) {
      return null;
    }

    return statistics.stream()
            .map(stat -> ObjectFactsStatistic.builder()
                    .setType(factTypeConverter.apply(stat.getFactTypeID()).toInfo())
                    .setCount(stat.getFactCount())
                    .setLastAddedTimestamp(stat.getLastAddedTimestamp())
                    .setLastSeenTimestamp(stat.getLastSeenTimestamp())
                    .build())
            .collect(Collectors.toList());
  }
}
