#include "pluginexample.h"

PluginExample::PluginExample() {
    qDebug() << "PluginExample created!";
}

QString PluginExample::pluginName() const
{
    return "Verdandi Plugin Example";
}

