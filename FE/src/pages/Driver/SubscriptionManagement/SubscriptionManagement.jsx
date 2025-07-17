import React from "react";
import styles from "./SubscriptionManagement.module.scss";
import iDrop from "@/assets/iDropGreen.svg";
import { transformSchedule } from "../../Parent/History/HistoryItem/transformSchedule";
import { removeCityPrefix } from "../../../utils/parseData";
import { Header } from "@/components/Header/Header";
import { getKidInfo } from "@/services/childrenAPI";
import { redirect } from "react-router-dom";
import { ScheduleList } from "../../../components/Schedule/ScheduleList";
import { postSubscribeRequest } from "../../../services/driverAPI";
import { useFetch } from "../../../hooks/useFetch";
import { Loader } from "../../../components/Loader/Loader";

export default function SubscriptionManagement() {
    const {
        loading,
        data: subscribeList,
        error,
        refetchData
    } = useFetch("/driver/subscribe/list", {});

    if (loading || !subscribeList) return <Loader />;
    if (error) redirect("/menu");

    const isHaveData = subscribeList.length > 0;

    if (!isHaveData) {
      return <div className={styles.headMessage}>현재 요청된 구독이 없어요</div>;
    }

    return (
        <div>
          <Header title="구독 요청" />
            subscribeList.map((subscription, index) => (
                <KidInformationBox key={index} {...subscription} />
            ))
        </div>
    );
}

function KidInformationBox({
    pickUpInfoId,
    childImage,
    childName,
    childBirth,
    childGender,
    startDate,
    endDate,
    startAddress,
    endAddress,
    parentName,
    parentPhoneNumber,
    status,
    schedule,
    refetchData
}) {
    startAddress = removeCityPrefix(startAddress);
    endAddress = removeCityPrefix(endAddress);
    schedule = transformSchedule(schedule);

    const handleSubscription = async (pickUpId, status) => {
        const postData = {
            pickUpInfoId: pickUpId,
            statusCode: status
        };
        await postSubscribeRequest(postData);
        // TODO 요청 처리 후 데이터 업데이트 고려하기
    };

    return (
        <div className={styles.content}>
            <div className={styles.kidInfo}>
                <img className={styles.kidImg} src={childImage || iDrop}></img>
                <div className={styles.infoBox}>
                    <div className={styles.profiles}>
                        <span className={styles.name}>{childName}</span>
                        <div
                            className={`${styles.status} ${status === "대기" ? styles.waiting : styles.accept}`}
                        >
                            {status}
                        </div>
                    </div>
                    <span className={styles.birthDay}>
                        {childGender}, {childBirth}
                    </span>

                    <span>
                        {startDate} ~ {endDate}
                    </span>
                    <span>
                        {parentName} / {parentPhoneNumber}
                    </span>
                </div>
            </div>
            <div>
                <span>
                    {startAddress} <br /> {"→"} {endAddress}
                </span>
            </div>
            <ScheduleList schedule={schedule} status={status} />
            <div className={styles.btnBox}>
                {status !== "승인" && (
                    <>
                        <button
                            className={styles.denyButton}
                            onClick={() =>
                                handleSubscription(
                                    pickUpInfoId,
                                    statusCode.refuse
                                )
                            }
                        >
                            거절
                        </button>
                        <button
                            className={styles.acceptButton}
                            onClick={() =>
                                handleSubscription(
                                    pickUpInfoId,
                                    statusCode.accept
                                )
                            }
                        >
                            수락
                        </button>
                    </>
                )}
            </div>
        </div>
    );
}

const statusCode = {
    accept: 1,
    refuse: 0
};

export async function fetchSubscribeList() {
    const fetchData = await getKidInfo("driver/subscribe/list");
    return fetchData;
}
