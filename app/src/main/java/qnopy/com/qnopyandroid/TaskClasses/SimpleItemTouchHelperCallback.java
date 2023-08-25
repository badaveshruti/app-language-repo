package qnopy.com.qnopyandroid.TaskClasses;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    // int dragFrom = -1;
    //int dragTo = -1;
    private boolean mOrderChanged;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();


       /* if(dragFrom == -1) {
            dragFrom =  fromPosition;
        }
        dragTo = toPosition;*/

        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());

        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }

   /* private void reallyMoved(int from, int to) {
        // I guessed this was what you want...

        mAdapter.onItemMove(from, to);
    }*/

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

  /*  @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            reallyMoved(dragFrom, dragTo);
        }

        dragFrom = dragTo = -1;
    }*/
}

