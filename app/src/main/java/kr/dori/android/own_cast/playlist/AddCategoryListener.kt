package kr.dori.android.own_cast.playlist

//dialog에서 recyclerView로 데이터를 전달해야 되는데, 문제는 dialog에서는 다른 객체의 인스턴스가 제한되어 있어서 어느 곳에서든 접근 가능한 인터페이스를 사용해서 구현해봤습니다.
interface AddCategoryListener {
    fun onCategoryAdded(categoryName: String)
}

