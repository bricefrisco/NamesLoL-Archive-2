export const parseResponse = (response: Response) => {
  return new Promise((res, rej) => {
    if (!response.ok) {
      response.text().then((err) => rej(err));
    } else {
      response.json().then(res)
    }
  })
};
