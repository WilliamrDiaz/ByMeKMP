package com.byme.app.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.byme.app.domain.model.User
import platform.MapKit.*
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKUserTrackingModeFollow
import platform.CoreLocation.CLLocationCoordinate2DMake
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapboxView(
    modifier: Modifier,
    professionals: List<User>,
    onProfessionalClick: (String) -> Unit
) {
    val mapView = remember { MKMapView() }

    // Creamos un Delegado para manejar eventos del mapa
    val delegate = remember {
        object : NSObject(), MKMapViewDelegateProtocol {
            private var didInitialZoom = false

            // Se ejecuta cada vez que el GPS actualiza tu posición
            override fun mapView(
                mapView: MKMapView,
                didUpdateUserLocation: MKUserLocation
            ) {
                val location = didUpdateUserLocation.location
                location?.let {
                    it.coordinate.useContents {
                        println("iOS Current Location -> Lat: $latitude, Lon: $longitude")
                    }
                }

                if (location != null && !didInitialZoom) {
                    // Creamos una región de 500m x 500m alrededor del usuario
                    val region = MKCoordinateRegionMakeWithDistance(
                        centerCoordinate = location.coordinate,
                        latitudinalMeters = 500.0,
                        longitudinalMeters = 500.0
                    )
                    mapView.setRegion(region, animated = true)
                    didInitialZoom = true // Solo lo hacemos la primera vez
                }
            }

            // Se ejecuta al tocar un marcador (pin) de profesional
            override fun mapView(
                mapView: MKMapView,
                didSelectAnnotationView: MKAnnotationView
            ) {
                val annotation = didSelectAnnotationView.annotation as? MKPointAnnotation ?: return
                val title = annotation.title
                // Buscamos al profesional por el nombre para obtener su ID
                professionals.find { "${it.name} ${it.lastname}" == title }?.let {
                    onProfessionalClick(it.id)
                }
                // Deseleccionamos para que se pueda volver a tocar
                mapView.deselectAnnotation(annotation, animated = true)
            }
        }
    }

    // Sincronizar marcadores (Annotations en iOS)
    LaunchedEffect(professionals) {
        mapView.removeAnnotations(mapView.annotations)
        professionals.forEach { prof ->
            if (prof.latitude != 0.0 && prof.longitude != 0.0) {
                val annotation = MKPointAnnotation().apply {
                    setCoordinate(CLLocationCoordinate2DMake(prof.latitude, prof.longitude))
                    setTitle("${prof.name} ${prof.lastname}")
                    setSubtitle(prof.category)
                }
                mapView.addAnnotation(annotation)
            }
        }
    }

    UIKitView(
        factory = {
            mapView.apply {
                showsUserLocation = true
                setDelegate(delegate)
                setUserTrackingMode(
                    MKUserTrackingModeFollow,
                    animated = true
                )
            }
        },
        modifier = modifier,
        update = { _ -> }
    )
}
