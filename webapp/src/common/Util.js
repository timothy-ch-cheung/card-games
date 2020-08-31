import {act} from "@testing-library/react";

export function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

export const flushPromises = () => new Promise(resolve => setTimeout(resolve))

function wait(amount = 0) {
    return new Promise(resolve => setTimeout(resolve, amount));
}

export async function actWait(amount = 0) {
    await act(async () => {
        await wait(amount);
    });
}