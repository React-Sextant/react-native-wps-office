import { NativeModules } from 'react-native';

const { WPSOffice } = NativeModules;

module.exports = {
    open: function(uri, MIMETypes="", options={}){
        return new Promise((resolve,reject) => {
            WPSOffice.open(uri,MIMETypes,options)
                .then(res=>resolve(res))
                .catch(err=>reject(err))
        })
    }
} ;
