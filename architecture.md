architecture.md
Driver & Session Lifecycle (Appium + Selenium)
Purpose
This section describes how driver instances are created, managed, and destroyed in the automation framework. Correct driver lifecycle management is critical for test stability, isolation, and future scalability—especially in mobile and streaming environments.

Driver Creation Strategy
	•	A single driver instance is created per test method
	•	Driver creation is centralized in a DriverFactory
	•	Tests never create driver instances directly
This ensures:
	•	Test isolation
	•	No state leakage between tests
	•	Predictable behavior in unstable environments

Appium + Selenium Interaction Model
	•	Appium acts as the automation bridge for the mobile environment
	•	Selenium WebDriver APIs are used to interact with web content
	•	UIAutomator2 is used as the underlying Android automation engine
From the test perspective:
	•	Tests interact with a standard WebDriver interface
	•	The underlying implementation may be Appium-backed
This abstraction allows:
	•	Cleaner test code
	•	Easier future platform expansion

Driver Ownership & Control
BaseTest Responsibility
	•	BaseTest controls:
	•	Driver initialization (@BeforeMethod)
	•	Driver teardown (@AfterMethod)
	•	Tests inherit from BaseTest
	•	Tests do not manage driver lifecycle
This guarantees consistent setup and cleanup across all tests.

Driver Access Pattern
	•	Driver instance is stored and accessed via a DriverManager
	•	Driver access is centralized and controlled
	•	No static driver usage inside test classes
This design avoids:
	•	Hidden dependencies
	•	Thread-safety issues
	•	Tight coupling between tests and infrastructure

Synchronization & Waiting Strategy
	•	Only explicit waits are used
	•	Waits are based on state, not time
	•	No use of Thread.sleep()
Examples of wait conditions:
	•	Element visibility
	•	UI state changes
	•	Loading indicator disappearance
	•	Player readiness state
This approach is essential for:
	•	Dynamic content
	•	Network variability
	•	Streaming playback stability

Cleanup & Session Termination
	•	Driver is quit after each test
	•	Appium session is explicitly closed
	•	Resources are released immediately
Benefits:
	•	Prevents session leaks
	•	Improves test reliability
	•	Keeps execution predictable

Connectivity Handling Considerations
	•	Network changes (connect / disconnect / switch) are handled via utility helpers
	•	Driver session remains active during connectivity changes
	•	UI recovery is validated after network restoration
This simulates real-world user conditions without restarting the app unnecessarily.

Design Decisions & Trade-Offs
Why one driver per test?
	•	Strong test isolation
	•	Easier debugging
	•	Fewer side effects
Why no parallel execution (yet)?
	•	PoC focus on stability and clarity
	•	Streaming apps are sensitive to timing
	•	Parallelism can be added later without refactoring
Why centralized lifecycle control?
	•	Enforces consistency
	•	Prevents misuse
	•	Mirrors production automation frameworks

Summary
The driver and session lifecycle is designed to be:
	•	Simple
	•	Predictable
	•	Robust under unstable conditions
This design reflects real-world automation practices used for complex mobile and streaming applications.

