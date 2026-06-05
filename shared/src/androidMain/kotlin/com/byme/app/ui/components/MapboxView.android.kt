package com.byme.app.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.byme.app.domain.model.User
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

@Composable
actual fun MapboxView(
    modifier: Modifier,
    professionals: List<User>,
    onProfessionalClick: (String) -> Unit
) {
    var circleManager: CircleAnnotationManager? by remember { mutableStateOf(null) }

    // Actualizar marcadores cuando la lista de profesionales cambie
    LaunchedEffect(professionals, circleManager) {
        val manager = circleManager ?: return@LaunchedEffect
        manager.deleteAll()
        
        professionals.forEach { prof ->
            if (prof.latitude != 0.0 && prof.longitude != 0.0) {
                val point = Point.fromLngLat(prof.longitude, prof.latitude)
                val circleOptions = CircleAnnotationOptions()
                    .withPoint(point)
                    .withCircleRadius(8.0)
                    .withCircleColor("#000000")
                    .withCircleStrokeWidth(4.0)
                    .withCircleStrokeColor("#FFFFFF")
                
                manager.create(circleOptions)
            }
        }
    }

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                // Cargar estilo básico
                mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)

                // Activar el punto de ubicación (Puck)
                location.updateSettings {
                    enabled = true
                    pulsingEnabled = true
                }

                // Centrar la cámara automáticamente la primera vez
                val positionListener = object : OnIndicatorPositionChangedListener {
                    override fun onIndicatorPositionChanged(point: Point) {
                        mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(point)
                                .zoom(13.0)
                                .build()
                        )
                        location.removeOnIndicatorPositionChangedListener(this)
                    }
                }
                location.addOnIndicatorPositionChangedListener(positionListener)

                // Inicializar el manager de marcadores
                val manager = annotations.createCircleAnnotationManager()
                manager.addClickListener { annotation ->
                    val clickedProf = professionals.find { 
                        it.latitude == annotation.point.latitude() && 
                        it.longitude == annotation.point.longitude() 
                    }
                    clickedProf?.let { onProfessionalClick(it.id) }
                    true
                }
                circleManager = manager
            }
        },
        modifier = modifier
    )
}
