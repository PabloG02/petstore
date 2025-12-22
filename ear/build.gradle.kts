// EAR module - Enterprise Application Archive packaging

plugins {
    id("ear")
}

dependencies {
    deploy(project(":jsf"))
    deploy(project(":rest"))
    deploy(project(":service"))
    earlib(project(":domain"))
}
