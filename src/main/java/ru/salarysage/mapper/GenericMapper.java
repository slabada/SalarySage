package ru.salarysage.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class GenericMapper {
    private final ModelMapper modelMapper;

    public GenericMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public <D> D convertToDto(Object source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
