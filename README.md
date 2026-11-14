# 🍽️ Sistema de Gestión de Pedidos - Cadena de Restaurantes
### Universidad Tecnológica del Perú (UTP) | Curso: Diseño de Patrones (100000SI47)
**Hito de Evaluación:** Avance 50% (Semana 14)  
**Líder Técnico:** Lincoln Vega Hidalgo  
**Stack Tecnológico:** Java SE 17+ / Java 25, NetBeans (Ant), MySQL JDBC  
**Meta Académica:** Implementación rigurosa de 15 Patrones de Diseño GOF y Principios SOLID.

---

## 📋 1. Matriz de Distribución de Patrones (15 Patrones GOF)

| # | Integrante | Módulo Asignado | Patrones GOF | Paquete en NetBeans | Estado |
|---|---|---|---|---|:---:|
| 1 | **Lincoln Vega Hidalgo** | Arquitectura & Fachada Principal | **Singleton**<br>**Facade** | `restaurante.config`<br>`restaurante.core.facade` | ✅ 100% |
| 2 | **Maria Isabel Ruiz Vasquez** | Menús, Platos y Combos | **Decorator**<br>**Composite** | `restaurante.patterns.structural.decorator`<br>`restaurante.patterns.structural.composite` | ✅ 100% |
| 3 | **Zoe Mendoza Lalopú** | Canales de Atención | **Factory Method**<br>**Abstract Factory** | `restaurante.patterns.creational.factorymethod`<br>`restaurante.patterns.creational.abstractfactory` | ✅ 100% |
| 4 | **Leonardo Garay Llaja** | Armado & Clonación | **Builder**<br>**Prototype** | `restaurante.patterns.creational.builder`<br>`restaurante.patterns.creational.prototype` | ✅ 100% |
| 5 | **Chris Grimaldo Cuya** | Pagos & Mensajería Multicanal | **Adapter**<br>**Bridge** | `restaurante.patterns.structural.adapter`<br>`restaurante.patterns.structural.bridge` | ✅ 100% |
| 6 | **Jairo Mejia Huerta** | Seguridad e Historial | **Proxy**<br>**Memento** | `restaurante.patterns.structural.proxy`<br>`restaurante.patterns.behavioral.memento` | ✅ 100% |
| 7 | **Etxin Malvas Basilio** | Cocina, Estados y Órdenes | **State**<br>**Observer**<br>**Command** | `restaurante.patterns.behavioral.state`<br>`restaurante.patterns.behavioral.observer`<br>`restaurante.patterns.behavioral.command` | ✅ 100% |

---

## 🏛️ 2. Arquitectura de Patrones Implementados

### 🏗️ Patrones Creacionales (*Creational*)
* **Singleton (`ConexionDB`):** Centraliza la conexión JDBC a MySQL con soporte de *fallback* offline para pruebas locales sin romper la ejecución.
* **Abstract Factory (`IServiciosCanalFactory`):** Familias de servicios según canal (`ServiciosSalonFactory` y `ServiciosDeliveryFactory`).
* **Factory Method (`PedidoFactory`):** Creación polimórfica de pedidos según el canal (`PedidoSalonFactory`, `PedidoDeliveryFactory`, `PedidoParaLlevarFactory`).
* **Builder (`PedidoBuilder`):** Construcción fluida y validada de pedidos paso a paso (`conCliente`, `enCanal`, `agregarItem`, `conPago`, `conNotas`).
* **Prototype (`CatalogoPlantillas` / `IPrototipo`):** Clonación profunda de pedidos base para agilizar la toma de pedidos repetitivos.

### 🏢 Patrones Estructurales (*Structural*)
* **Facade (`RestauranteFacade`):** Interfaz simplificada y unificada de alto nivel que orquesta los 15 subsistemas del restaurante.
* **Composite (`ComboMenuComposite`):** Jerarquía parte-todo que permite tratar platos individuales y combos anidados de forma uniforme con cálculo recursivo de descuentos.
* **Decorator (`PlatoDecorator`):** Decoradores concretos para personalización dinámica:
  * `ExtraQuesoDecorator` (+S/ 3.00)
  * `PorcionPapasExtraDecorator` (+S/ 4.50)
  * `SinCebollaDecorator` (S/ 0.00)
* **Adapter (`IPago`):** Integración de pasarelas heterogéneas:
  * `YapePaymentAdapter` (Billetera móvil BCP)
  * `TarjetaPaymentAdapter` (Pasarela Visa/Mastercard)
* **Bridge (`Notificacion`):** Desacopla la lógica de negocio de los canales técnicos:
  * Abstracciones: `NotificacionPedidoListo`, `NotificacionComprobantePago`
  * Implementadores: `WhatsAppImplementador`, `EmailImplementador`
* **Proxy (`AlmacenProxySeguro`):** Control de acceso por roles (Mozo solo consulta; Admin y Cocina descuentan insumos) y caché local de stock.

### 🔄 Patrones de Comportamiento (*Behavioral*)
* **State (`IEstadoPedido`):** Ciclo de vida dinámico del pedido: `RegistradoState` ➔ `EnCocinaState` ➔ `ListoState` ➔ `EntregadoState`.
* **Observer (`IObservadorPedido`):** Monitores reactivos desacoplados:
  * `CocinaMonitorObserver` (Avisos a brigada de cocina)
  * `CajaMonitorObserver` (Alertas para facturación y cobro)
  * `DeliveryRastreadorObserver` (Rastreo GPS y asignación de despachos)
* **Command (`IComando`):** Encapsulación de órdenes con soporte de cola y reversión:
  * `EnviarCocinaCommand`, `CancelarPedidoCommand`
  * Invocador: `GestorComandosRestaurante`
* **Memento (`PedidoMemento`):** Instantáneas inmutables del pedido gestionadas por `HistorialPedidoCaretaker` para operaciones de `Undo` (deshacer).

---

## 🚀 3. Compilación y Ejecución

### Opción A: Desde Terminal (PowerShell / CMD)
```powershell
# 1. Compilar todas las clases
javac -cp "SistemaRestaurante/lib/mysql-connector-j-8.0.33.jar" (Get-ChildItem -Recurse "SistemaRestaurante/src/*.java" | Select-Object -ExpandProperty FullName) -d "SistemaRestaurante/build/classes"

# 2. Ejecutar la demostración maestra
java -cp "SistemaRestaurante/build/classes;SistemaRestaurante/lib/mysql-connector-j-8.0.33.jar" restaurante.Main
```

### Opción B: Desde Apache NetBeans
1. Abrir **NetBeans IDE**.
2. Ir a **File** > **Open Project...** y seleccionar la carpeta `SistemaRestaurante`.
3. Hacer clic derecho sobre el proyecto > **Clean and Build**.
4. Presionar **Run** (F6) para ejecutar `restaurante.Main`.

---

## 🎯 4. Principios SOLID Aplicados

* **S (Single Responsibility):** Cada clase tiene una única responsabilidad bien acotada (ej. los monitores solo reaccionan al cambio de estado, los adaptadores solo transforman firmas de pago).
* **O (Open/Closed):** Nuevos canales, métodos de pago o decoradores se agregan creando nuevas clases sin modificar el código existente.
* **L (Liskov Substitution):** Cualquier subclase (`ComboMenuComposite`, `PlatoSimple`, `ExtraQuesoDecorator`) puede sustituir a `IComponenteMenu` sin alterar el comportamiento del sistema.
* **I (Interface Segregation):** Interfaces pequeñas y cohesivas (`IPago`, `IComando`, `IObservadorPedido`, `IEstadoPedido`, `IPrototipo`).
* **D (Dependency Inversion):** Los módulos de alto nivel (como la Fachada y el Builder) dependen de abstracciones e interfaces, nunca de implementaciones concretas.
