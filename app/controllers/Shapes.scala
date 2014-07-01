package controllers

//object Shapes  extends Controller with PickleController
//{
//
//  val shapeRes = new IRI("http://shape.org")
//  val title = (WI.PLATFORM / "title").iri
//  val text = (WI.PLATFORM / "text").iri
//  val completed = (WI.PLATFORM / "completed").iri
//  val task = (WI.PLATFORM / "task").iri
//
//
//  val writePaper = PropertyModel(IRI(WI.PLATFORM /"WritePaper"), title -> StringLiteral("Write paper"),  text->StringLiteral("I have to write agind as a disease paper") , completed->BooleanLiteral(false) , RDF.TYPE-> task)
//  val makeWebsite = PropertyModel(IRI(WI.PLATFORM /"MakeWebsite"), title -> StringLiteral("Make a website"),  text->StringLiteral("I have to make Longevity Ukraine website work") , completed->BooleanLiteral(true), RDF.TYPE-> task)
//  val doCRM = PropertyModel(IRI(WI.PLATFORM /"MakeCRM"), title -> StringLiteral("Make ILA CRM"),  text->StringLiteral("I have to make CRM for ILA") , completed->BooleanLiteral(false), RDF.TYPE-> task)
//
//
//  var items:List[PropertyModel] = writePaper::makeWebsite::doCRM::Nil
//
//
//  override def onCreate(createMessage: Create)(implicit request:RequestType): Result = {
//    items = this.items ++ createMessage.models.toList
//    Ok(rp.pickle(true)).as("application/json")
//  }
//
//  override def onUpdate(updateMessage: Update)(implicit request:RequestType): Result =
//  {
//    val res = updateMessage.models.map(_.resource)
//    items = items.filterNot(i=>i.resource==res)++updateMessage.models
//    Ok(rp.pickle(true)).as("application/json")
//  }
//
//  override def onRead(readMessage: Read)(implicit request:RequestType): Result = {
//    val res = items.filter(i=>readMessage.resources.contains(i.resource))
//    Ok(rp.pickle(res)).as("application/json")
//  }
//
//  override def onDelete(deleteMessage: Delete)(implicit request:RequestType): Result = {
//
//    items = items.filterNot(kv=>deleteMessage.res.contains(kv.resource))
//    Ok(rp.pickle(true)).as("application/json")
//
//  }
//
//  def endpoint() = UserAction(this.pickle[ReadMessage]()){implicit request=>
//    this.onMessage(request.body)
//
//  }
//
//  override def onSelect(createMessage: SelectQuery)(implicit request: RequestType): Result = {
//    rp.registerPicklers()
//    val data = rp.pickle(items)
//    Ok(data).as("application/json")
//  }
//}


