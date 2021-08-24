import {useLocation} from "react-router-dom";

export const parseResponse = (response: Response) => {
  return new Promise((res, rej) => {
    if (!response.ok) {
      response.text().then((err) => rej(err));
    } else {
      response.json().then(res)
    }
  })
};

export const useParams = () => {
  return new URLSearchParams(useLocation().search);
}

export const navigate = (history: any, time: number | string | null, backwards: boolean | string | null, nameLength: number | string | null) => {
  let url = '/summoners';
  url += '?time=' + time;
  url += '&backwards=' + backwards;
  if (nameLength) {
    url += '&nameLength=' + nameLength;
  }
  history.push(url);
}
