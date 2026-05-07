# INFORME DE PROYECTO DE FIN DE CICLO

---

## FuerzApp — Plataforma SaaS de Gestión de Gimnasios

---

**Autor:** TODO (Nombre completo del alumno)

**Tutor/Profesor:** TODO (Nombre del profesor tutor)

**Centro educativo:** TODO (Nombre del centro)

**Ciclo formativo:** Desarrollo de Aplicaciones Web (DAW)

**Curso académico:** TODO (Ejemplo: 2025–2026)

**Fecha de entrega:** TODO (Fecha de entrega del proyecto)

---

---

# ÍNDICE

1. [Introducción](#1-introducción)
2. [Origen y contextualización](#2-origen-y-contextualización)
3. [Objetivo general](#3-objetivo-general)
4. [Objetivos específicos](#4-objetivos-específicos)
5. [Tarea 1 — Análisis y diseño del sistema](#5-tarea-1--análisis-y-diseño-del-sistema)
   - 5.1 Análisis de requisitos
   - 5.2 Diseño de la base de datos
   - 5.3 Diseño de la arquitectura del sistema
   - 5.4 Diseño de la API REST
   - 5.5 Diseño de la interfaz de usuario (wireframes)
6. [Tarea 2 — Desarrollo del backend](#6-tarea-2--desarrollo-del-backend)
   - 6.1 Configuración del entorno y estructura del proyecto
   - 6.2 Modelado de entidades y persistencia
   - 6.3 Implementación de servicios y lógica de negocio
   - 6.4 Autenticación y seguridad con JWT
   - 6.5 Integración con Stripe (pagos y facturación)
   - 6.6 Pruebas unitarias del backend
7. [Tarea 3 — Desarrollo del frontend](#7-tarea-3--desarrollo-del-frontend)
   - 7.1 Configuración del proyecto Angular
   - 7.2 Módulo de autenticación
   - 7.3 Panel de administrador de plataforma
   - 7.4 Panel de propietario de gimnasio
   - 7.5 Panel de entrenador
   - 7.6 Panel de cliente
8. [Tarea 4 — Integración, pruebas y despliegue](#8-tarea-4--integración-pruebas-y-despliegue)
   - 8.1 Integración frontend–backend
   - 8.2 Pruebas de integración y funcionales
   - 8.3 Despliegue en producción
9. [Recursos Humanos](#9-recursos-humanos)
10. [Recursos Materiales](#10-recursos-materiales)
11. [Cronograma](#11-cronograma)
12. [Presupuesto](#12-presupuesto)
13. [Política de seguimiento y evaluación](#13-política-de-seguimiento-y-evaluación)
14. [Futuras mejoras](#14-futuras-mejoras)
15. [Anexos](#15-anexos)
16. [Bibliografía](#16-bibliografía)

---

# 1. Introducción

El presente documento constituye la memoria técnica del Proyecto de Fin de Ciclo correspondiente al ciclo formativo de **Grado Superior en Desarrollo de Aplicaciones Web (DAW)**. El proyecto desarrollado se denomina **FuerzApp** y consiste en una plataforma SaaS (*Software as a Service*) orientada a la gestión integral de gimnasios y centros deportivos.

La motivación principal del proyecto surge de la necesidad real que tienen muchos gimnasios de pequeño y mediano tamaño de disponer de una herramienta de gestión moderna, accesible y asequible. Las soluciones existentes en el mercado suelen ser costosas, difíciles de escalar o no contemplan la gestión multilocal (un mismo propietario con varios gimnasios). FuerzApp nace como respuesta directa a estas carencias, con un enfoque comercial real: el objetivo a largo plazo es ofrecer la plataforma a gimnasios reales bajo un modelo de suscripción mensual.

El sistema implementado es una aplicación web de arquitectura desacoplada. El **backend** es una API REST desarrollada con **Java 21** y **Spring Boot 3.3.0**, con persistencia en **MySQL 8** a través de Spring Data JPA/Hibernate, seguridad implementada mediante **JWT** y pagos integrados con **Stripe**. El **frontend** es una *Single Page Application* (SPA) construida con **Angular 19.2** en su modalidad de componentes independientes (*Standalone Components*), con **Angular Material 19.2** como librería de interfaz de usuario.

La plataforma gestiona cuatro roles diferenciados: **administrador de plataforma** (gestión global del SaaS), **propietario de gimnasio** (gestión de su gimnasio o gimnasios), **entrenador** y **cliente**. Cada rol dispone de un panel de control propio con funcionalidades adaptadas a sus necesidades.

A lo largo de este informe se documentan todas las fases del proyecto: el análisis y diseño inicial, el desarrollo del backend y del frontend, la integración entre ambas capas, las pruebas realizadas, el despliegue y las decisiones técnicas y arquitectónicas más relevantes adoptadas durante el proceso.

---

# 2. Origen y contextualización

## 2.1 Contexto del sector

El sector del fitness en España ha experimentado un crecimiento sostenido en los últimos años. Según datos del *European Health & Fitness Market Report*, España se sitúa entre los cinco primeros mercados europeos de gimnasios por número de instalaciones y abonados. Este crecimiento ha generado una demanda creciente de herramientas de gestión que permitan a los gimnasios optimizar sus operaciones, fidelizar a sus clientes y modernizar sus procesos administrativos.

Sin embargo, existe una brecha significativa en la oferta de software de gestión para pequeños y medianos gimnasios. Las grandes plataformas del sector (como Mindbody, Glofox o Virtuagym) están orientadas a centros de gran tamaño y tienen un coste elevado que las hace inaccesibles para la mayoría de los gimnasios independientes. La alternativa habitual son soluciones genéricas de gestión o, en muchos casos, el uso de hojas de cálculo y gestión manual, con el consiguiente riesgo de errores y pérdida de eficiencia.

## 2.2 Oportunidad identificada

Durante la fase de investigación previa al desarrollo del proyecto, se identificaron varias necesidades concretas no cubiertas adecuadamente por las soluciones existentes:

- **Gestión multigimnasio:** Un mismo propietario puede gestionar varios centros desde una única cuenta, sin necesidad de duplicar configuraciones.
- **Gestión de entrenamientos personalizada:** La plataforma permite a los entrenadores crear planes de entrenamiento individuales o grupales y asignarlos a clientes específicos, con seguimiento de sesiones.
- **Integración de pagos nativa:** La gestión de suscripciones de clientes y el cobro automatizado mediante pasarela de pago se integran directamente en la plataforma, eliminando la dependencia de sistemas externos.
- **Multirol y multitenant:** La arquitectura multitenancy permite que cada gimnasio opere de forma completamente aislada dentro de la misma instalación del software.
- **Facturación automática:** Cada pago realizado genera una factura con formato normalizado de forma automática, sin intervención manual.

## 2.3 Contexto académico

Este proyecto se enmarca en el módulo de **Proyecto** del ciclo formativo de Grado Superior en Desarrollo de Aplicaciones Web, cuyo objetivo es que el alumnado integre y demuestre las competencias adquiridas a lo largo del ciclo en un proyecto de desarrollo de software completo y con valor real. El proyecto ha sido concebido desde el principio con una orientación comercial, lo que implica que las decisiones técnicas no solo tienen en cuenta los aspectos académicos, sino también la viabilidad, escalabilidad y mantenibilidad del sistema a largo plazo.

---

# 3. Objetivo general

El objetivo general del proyecto es **diseñar, desarrollar e implantar una plataforma web SaaS de gestión integral de gimnasios**, denominada **FuerzApp**, que permita a propietarios de centros deportivos gestionar sus instalaciones, personal, clientes, suscripciones, entrenamientos y pagos desde una única herramienta accesible desde cualquier navegador web, sin necesidad de instalación de software adicional.

La plataforma debe ser capaz de:

- Soportar múltiples gimnasios y múltiples usuarios con roles diferenciados en un entorno multitenancy seguro.
- Automatizar los procesos de cobro y facturación mediante integración con la pasarela de pago Stripe.
- Proporcionar herramientas de planificación y seguimiento de entrenamientos para entrenadores y clientes.
- Ofrecer una experiencia de usuario moderna, intuitiva y adaptada a cada perfil de usuario.
- Garantizar la seguridad de los datos mediante autenticación basada en tokens JWT y control de acceso por roles.

---

# 4. Objetivos específicos

Los objetivos específicos del proyecto se derivan del objetivo general y permiten desglosar el trabajo en metas concretas y medibles:

**OE-01 — Diseño del modelo de datos:**
Diseñar un esquema relacional completo que dé soporte a todas las entidades del dominio (gimnasios, usuarios, suscripciones, entrenamientos, pagos, facturas, etc.) y que garantice la integridad referencial y el aislamiento de datos entre gimnasios.

**OE-02 — Implementación de la API REST:**
Desarrollar una API REST con Spring Boot que exponga endpoints claros y seguros para todas las operaciones del sistema, siguiendo las convenciones REST y documentando los contratos de cada endpoint.

**OE-03 — Autenticación y autorización:**
Implementar un sistema de autenticación sin estado (*stateless*) basado en tokens JWT, con control de acceso por roles (ADMIN_PLATAFORMA, PROPIETARIO, ENTRENADOR, CLIENTE) mediante Spring Security.

**OE-04 — Gestión de suscripciones y pagos:**
Integrar la pasarela de pago Stripe mediante Checkout Sessions y webhooks para gestionar el ciclo de vida completo de las suscripciones de los clientes, incluyendo la generación automática de facturas.

**OE-05 — Gestión de entrenamientos:**
Implementar un sistema completo de gestión de ejercicios, planes de entrenamiento y sesiones, con soporte para tres tipos de entrenamiento (GRUPAL, INDIVIDUAL, ESPECIFICO) y control de capacidad de sesiones.

**OE-06 — Desarrollo del frontend por roles:**
Desarrollar cuatro paneles de control diferenciados (administrador, propietario, entrenador, cliente) con Angular 19 y Angular Material, cada uno con las funcionalidades específicas de su rol.

**OE-07 — Multigimnasio y multitenant:**
Implementar la funcionalidad de multigimnasio en el frontend mediante un servicio de contexto de gimnasio activo (`GimnasioContextService`) y garantizar el aislamiento de datos en el backend por gimnasio.

**OE-08 — Pruebas unitarias:**
Desarrollar una batería de pruebas unitarias del backend con JUnit 5 y Mockito que cubra los casos de uso críticos de la lógica de negocio (34 tests distribuidos en 4 clases de test).

**OE-09 — Despliegue:**
Desplegar la aplicación en un entorno accesible públicamente, con la API backend y el frontend disponibles en URLs persistentes.

**OE-10 — Documentación técnica:**
Elaborar la documentación técnica del proyecto que recoja todas las decisiones de diseño, la arquitectura del sistema, los endpoints de la API y las instrucciones de instalación y despliegue.

---
