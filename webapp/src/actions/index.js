const Actions = () =>
{
    const set = value => {
        return {
            type: 'SET',
            payload: value
        }
    }

    const reset = () => {
        return {
            type: 'RESET'
        }
    }
}
export default Actions;