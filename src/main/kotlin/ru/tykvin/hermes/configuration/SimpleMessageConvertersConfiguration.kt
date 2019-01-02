package ru.tykvin.hermes.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class SimpleMessageConvertersConfiguration(
        private val om: ObjectMapper
) : WebMvcConfigurer {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(FormHttpMessageConverter())
        converters.add(MappingJackson2HttpMessageConverter(om))
        converters.add(StringHttpMessageConverter())
    }

}