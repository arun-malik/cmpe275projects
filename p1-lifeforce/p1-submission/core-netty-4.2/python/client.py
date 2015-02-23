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
    host = raw_input("IP:")
    port = raw_input("Port:")

    #port = 5573 #raw_input("Port:")
    port = int(port)
    whoAmI = 1;

    input = raw_input("Select your desirable action:\n1.SaveImage\n2.GetImage\n3.DeleteImage ::\n")

    if input == "1":
        fh = open('picture.JPG','rb')
        dataFile = fh.read()
        iname="picture"
        ##print data
        fh.close()
        data = base64.b64encode(dataFile)
        saveimageJob = buildSaveImageJob(iname, data, 1)
        result = sendMsg(saveimageJob, port, host)
        if result.header.photoHeader.responseFlag == 0:
            print("Saved successfully !!")
        elif result.header.photoHeader.responseFlag == 1:
            print("Image not saved !!")
        print result.body.photoPayload.uuid


    elif input == "2":
        gImage = raw_input("Enter UUID to get Image:\n ")
        getImagejob=buildGetImageJob(gImage)
        result = sendMsg(getImagejob, port, host)
        if result.header.photoHeader.responseFlag == 0:
            print("Following Image is found ")
            print result.body.photoPayload.uuid
            print("The name of image retrieved ::\n")
            print result.body.photoPayload.name
            print("The retrieved image::\n")
            print result.body.photoPayload.data

        elif result.header.photoHeader.responseFlag == 1:
            print("Image not found !!")


    elif input == "3":
        dImage= raw_input("Enter UUID for image to delete:\n ")
        delImagejob=buildDeleteImageJob(dImage)
        result=sendMsg(delImagejob, port, host)
        print("The response flag received:\n")
        print result.header.photoHeader.responseFlag
        if result.header.photoHeader.responseFlag == 0:
            print("The image has been deleted successfully !!")
        elif result.header.photoHeader.responseFlag == 1:
            print("The image was not deleted successfully !!")


def getBroadcastMsg(port):
    # listen for the broadcast from the leader"

    sock = socket.socket(socket.AF_INET,  # Internet
                        socket.SOCK_DGRAM)  # UDP

    sock.bind(('', port))

    data = sock.recv(1024)  # buffer size is 1024 bytes
    return data

def buildPing(tag, number):

    r = comm_pb2.Request()

    r.body.ping.tag = str(tag)
    r.body.ping.number = number


    r.header.originator = 0
    r.header.tag = str(tag + number + int(round(time.time() * 1000)))
    r.header.routing_id = comm_pb2.Header.PING
    r.header.toNode = 1

    msg = r.SerializeToString()
    return msg


def buildNS():
    r = comm_pb2.Request()

    r.body.space_op.action = comm_pb2.NameSpaceOperation.ADDSPACE


    r.header.originator = 0
    r.header.tag = str(int(round(time.time() * 1000)))
    r.header.routing_id = comm_pb2.Header.NAMESPACES

    m = r.SerializeToString()
    return m
