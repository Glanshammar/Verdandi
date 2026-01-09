#ifndef PLUGINEXAMPLE_H
#define PLUGINEXAMPLE_H

#include "PluginExample_global.h"
#include "../plugininterface.h"

class PLUGINEXAMPLE_EXPORT PluginExample:
                                public QObject,
                                public PluginInterface
{
    Q_OBJECT
    Q_INTERFACES(PluginInterface)
    Q_PLUGIN_METADATA(IID PluginInterfaceIID)

public:
    PluginExample();
};
#endif // PLUGINEXAMPLE_H
