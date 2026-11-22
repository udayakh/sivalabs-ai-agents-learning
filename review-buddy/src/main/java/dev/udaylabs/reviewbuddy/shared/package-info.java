/**
 * Shared kernel: domain independent building blocks. Declared as an OPEN module so that every
 * other module may use all of its types (including nested packages).
 */
@ApplicationModule(type = ApplicationModule.Type.OPEN)
package dev.udaylabs.reviewbuddy.shared;

import org.springframework.modulith.ApplicationModule;
