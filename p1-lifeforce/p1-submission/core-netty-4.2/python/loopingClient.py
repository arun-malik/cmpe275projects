import comm_pb2
import socket
import time
import struct
import base64


def buildSaveImageJob(iname, data, ownerId):

    jobId = str(int(round(time.time() * 1000)))
    r= comm_pb2.Request()

    r.header.photoHeader.requestType = 1    #1 is for write
    r.body.photoPayload.name=iname
    r.body.photoPayload.data = data

    r.header.originator = 0
    r.header.routing_id = comm_pb2.Header.JOBS
    r.header.toNode = 1
    msg = r.SerializeToString()
    return msg

def buildGetImageJob(gImage):
    r= comm_pb2.Request()

    r.header.photoHeader.requestType = 0    #0 is the requestType for read
    r.body.photoPayload.uuid=gImage

    r.header.originator = 0
    r.header.routing_id = comm_pb2.Header.JOBS
    r.header.toNode = 1
    msg=r.SerializeToString()
    return msg

def buildDeleteImageJob(dImage):
    r= comm_pb2.Request()

    r.header.photoHeader.requestType = 2    #2 is the requestType for delete
    r.body.photoPayload.uuid=dImage

    r.header.originator = 0
    r.header.routing_id = comm_pb2.Header.JOBS
    r.header.toNode = 1
    msg=r.SerializeToString()
    return msg

def sendMsg(msg_out, port, host):
    s = socket.socket()
#    host = socket.gethostname()
#    host = "192.168.0.87"

    s.connect((host, port))
    msg_len = struct.pack('>L', len(msg_out))
    s.sendall(msg_len + msg_out)
    len_buf = receiveMsg(s, 4)
    msg_in_len = struct.unpack('>L', len_buf)[0]
    msg_in = receiveMsg(s, msg_in_len)

    r = comm_pb2.Request()
    r.ParseFromString(msg_in)
#    print msg_in
#    print r.body.job_status
#    print r.header.reply_msg
#    print r.body.job_op.data.options
    s.close
    return r
def receiveMsg(socket, n):
    buf = ''
    while n > 0:
        data = socket.recv(n)
        if data == '':
            raise RuntimeError('data not received!')
        buf += data
        n -= len(data)
    return buf



if __name__ == '__main__':
    # msg = buildPing(1, 2)
    # UDP_PORT = 8080
    # serverPort = getBroadcastMsg(UDP_PORT)
    host = "localhost" #raw_input("IP:")
    port = 5572 #raw_input("Port:")

    #port = 5572 #raw_input("Port:")
    port = int(port)
    whoAmI = 1;

    fh = open('picture.JPG','rb')
    dataFile = fh.read()
    iname="picture"
    ##print data
    fh.close()
    data = base64.b64encode(dataFile)

    for x in range(0, 1000):
        print "We're on time %d" % (x)
        saveimageJob = buildSaveImageJob(iname, data, 1)
        result = sendMsg(saveimageJob, port, host)
        if result.header.photoHeader.responseFlag == 0:
            print("Saved successfully !!")
        elif result.header.photoHeader.responseFlag == 1:
            print("Image not saved !!")
        print result.body.photoPayload.uuid
