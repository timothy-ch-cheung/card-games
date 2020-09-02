export function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

export const flushPromises = () => new Promise(resolve => setTimeout(resolve))