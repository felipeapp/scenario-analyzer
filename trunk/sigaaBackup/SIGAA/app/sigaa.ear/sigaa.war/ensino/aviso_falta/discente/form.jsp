<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="avisoFalta"></a4j:keepAlive>
	
	<h2>
		<ufrn:subSistema /> &gt; Avisar Falta do Professor
	</h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>A notificação é completamente anônima. Nenhuma informação a seu respeito será revelada para qualquer docente.</p>			
		
		<p><b>Sua privacidade está garantida.</b> O Chefe do Departamento responsável pela disciplina e a PROGRAD receberão sua notificação anônima.</p>
	</div>

	<h:form id="formDenuncia">

		<table class="formulario" width="80%">
			<caption>Informações sobre a notificação</caption>
			<tbody>
			<tr>
				<th class="required">Disciplina:</th>
				<td>
					<h:selectOneMenu id="disciplina" value="#{avisoFalta.obj.dadosAvisoFalta.turma.id}" onchange="submit()" valueChangeListener="#{avisoFalta.carregarCombos}">
						<a4j:status>
							<f:facet name="start"><h:graphicImage value="/img/indicator.gif" /></f:facet>
						</a4j:status>
						<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0"/>
						<f:selectItems value="#{portalDiscente.turmasAbertasCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Professor:</th>
				<td>
					<h:selectOneMenu id="docente" value="#{avisoFalta.obj.dadosAvisoFalta.docente.id}" onchange="verificaOutroProfessor()">
						<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0" />
						<f:selectItems value="#{avisoFalta.docenteCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Data da Aula:</th>
				<td>
					<h:selectOneMenu value="#{avisoFalta.dataAulaStr}" id="data" rendered="#{avisoFalta.possuiDatasDeAulas}">
							<f:selectItem itemLabel="--> SELECIONE <--"/>						
							<f:selectItems value="#{avisoFalta.aulasCombo}" />
						</h:selectOneMenu>
					<t:inputCalendar value="#{avisoFalta.obj.dadosAvisoFalta.dataAula}" size="10" maxlength="10" 
							 onkeypress="return formataData(this,event)" popupDateFormat="dd/MM/yyyy"
							 id="Data" renderAsPopup="true" renderPopupButtonAsImage="true" 
							 rendered="#{!avisoFalta.possuiDatasDeAulas}" >
					</t:inputCalendar>
					
				</td>
			</tr>
			<tr>
				<th class="required">Observações:</th>
				<td>
					<h:inputTextarea id="observacao" value="#{avisoFalta.obj.observacao}" style="width: 95%" />
				</td>
			</tr>	
			</tbody>				
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btn_notificar" value="Notificar" action="#{avisoFalta.cadastrar}" />
						<h:commandButton id="btn_cancelar" value="Cancelar"  action="#{avisoFalta.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
