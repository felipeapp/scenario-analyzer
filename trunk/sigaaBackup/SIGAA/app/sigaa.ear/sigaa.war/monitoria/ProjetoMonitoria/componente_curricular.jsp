<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
<h2><ufrn:subSistema /> > Cadastro de Projeto de Ensino</h2>
<h:form id="formCadastroComponentes">


	<div class="descricaoOperacao">
		<table width="100%">
			<tr>
				<td> Digite o nome do Componente Curricular e espere o sistema abrir uma caixa para seleção. Selecione então
					e clique em Adicionar Componente Curricular. Se preferir, digite o código da disciplina e clique também
					em Adicionar Componente Curricular. <br/>				
				</td>
				<td><%@include file="passos_projeto.jsp"%></td>
			</tr>
		</table>
	</div>

	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:inputHidden value="#{projetoMonitoria.confirmButton}"/>
	<h:inputHidden value="#{projetoMonitoria.obj.id}"/>

	<table class="formulario" width="100%" cellpadding="3" id="tabela1">
	<caption class="formulario"> Selecionar Componentes Curriculares para o Projeto </caption>

	<tr>
		<td>
			<table class="subFormulario" width="100%">
				<caption>Adicionar Componente Curricular</caption>

				<tr><td> </td></tr>
				<tr>
						<th><input type="hidden" id="id" name="id" value="0"/>Por Nome: </th>
					 	<td>
					 		<input type="text" id="disciplina" name="disciplina" size="75" onkeyup="CAPS(this)"/>
							<span id="indicator" style="display:none; "> <img	src="/sigaa/img/indicator.gif" /> </span>

							<ajax:autocomplete source="disciplina" target="id"
							baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="nivel=G"
							parser="new ResponseXmlToHtmlListParser()" />
					 	</td>
				 </tr>
				 <tr><td> </td></tr>
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="Adicionar Componente Curricular" action="#{projetoMonitoria.adicionaDisciplina}" id="btAdicionarComponente" />
						</td>
					</tr>
				</tfoot>
			</table>
	</td></tr>


		<tr>
			<td>
				<div class="infoAltRem">
				    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Componente Curricular do Projeto de Ensino<br/>
				</div>			
			</td>
		</tr>


	<tr>
		<td>
		<table class="subFormulario" width="100%">
		<caption>Lista de Componentes Curriculares do Projeto <span class="required"></span></caption>
			<c:if test="${ not empty projetoMonitoria.obj.componentesCurriculares }">
				<tr><td>
					<t:dataTable value="#{projetoMonitoria.obj.componentesCurriculares}" var="compCurricular" rowClasses="linhaPar,linhaImpar" width="100%" id="comp">
						<t:column>

							<f:verbatim><table width="100%"><tr><td width='98%'></f:verbatim>
							
							<f:verbatim><b>Componente Curricular:&nbsp;&nbsp;</b></f:verbatim>
								<h:outputText value="#{compCurricular.disciplina.codigoNome}<br/>" escape="false"/>
						
							<f:verbatim></td><td></f:verbatim>
														
							<h:commandLink action="#{projetoMonitoria.removeDisciplina}">
								<f:param name="idDisciplina" value="#{compCurricular.disciplina.id}"/>
								<h:graphicImage url="/img/delete.gif" alt="Exluir Este Componente do Projeto" title="Remover Componente Curricular do Projeto de Ensino"/>
							</h:commandLink>							
							
							<f:verbatim></td></tr><tr><td colspan="2"></f:verbatim>
							
							<f:verbatim>Períodos de Oferecimento da Monitoria:&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
							<h:selectBooleanCheckbox value="#{compCurricular.semestre1}" id="checkSemestre1" />
							<f:verbatim>1º Semestre</f:verbatim>
							<f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
							<h:selectBooleanCheckbox value="#{compCurricular.semestre2}" id="checkSemestre2" />
							<f:verbatim>2º Semestre</f:verbatim>
							<f:verbatim></td></tr></f:verbatim>
							<f:verbatim><tr><td></td></tr></f:verbatim>
							<f:verbatim>
								<tr>
									<td colspan="2" class="subFormulario">Plano de Trabalho</td>
								</tr>
							</f:verbatim>	
							<f:verbatim><tr><td colspan="2"></f:verbatim>
							<f:verbatim><span class="required">Carga-horária semanal destinada ao projeto:</span></f:verbatim>
							<h:inputText id="chDestinadaProjeto" value="#{compCurricular.chDestinadaProjeto}" maxlength="2" size="2" onkeyup="formatarInteiro(this)"/>
							<f:verbatim></td></tr><tr><td colspan="2"></f:verbatim>
							
							<f:verbatim><span class="required">Atividades desenvolvidas pelo monitor:</span></f:verbatim><br />
							<h:inputTextarea value="#{compCurricular.planoTrabalho}"  style="width:98%" rows="5" id="planoTrabalho" />
							<f:verbatim></td></tr><tr><td colspan="2"></f:verbatim>
							
							<f:verbatim><span class="required">Avaliação do Monitor:</span></f:verbatim>
							<h:inputTextarea id="avaliacaoMonitor" value="#{compCurricular.avaliacaoMonitor}" style="width:98%" rows="5" />
				
							<f:verbatim></td></tr></table></f:verbatim>

						</t:column>

					</t:dataTable>
				</td></tr>
			</c:if>
			<c:if test="${ empty projetoMonitoria.obj.componentesCurriculares }">
				<tr>
					<td align="center"><font color="red">Não há Componentes Curriculares Adicionados!</font></td>
				</tr>
			</c:if>
			</table>
		</td>
	</tr>


		<tr>
			<td><br/></td>
		</tr>


	<tfoot>
		<tr>
			<td colspan="2">				
				<h:commandButton value="Gravar Proposta" action="#{ projetoMonitoria.cadastrarParcialComponentesCurriculares }" title="Gravar Proposta para Continuar Depois." id="btGravarComponentes" />
				<h:commandButton value="<< Voltar" action="#{ projetoMonitoria.passoAnterior }" id="btVoltar" />				
				<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}" id="btCancelar" onclick="#{confirm}"/>				
				<h:commandButton value="Avançar >>" action="#{projetoMonitoria.submeterComponentesCurriculares}" id="btSubmeterComponentesCurriculares" />
			</td>
		</tr>
	</tfoot>
	</table>
	
</h:form>
	
	<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>

</f:view>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "60%", height : "200", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image,fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons2 : "",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>