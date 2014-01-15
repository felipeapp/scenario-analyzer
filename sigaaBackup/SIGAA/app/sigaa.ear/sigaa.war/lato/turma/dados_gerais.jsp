<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript">
function adicionarReserva(event) {
	var obj =  event.target ? event.target : event.srcElement;
	var tr = getEl(obj.parentNode.parentNode.parentNode);
	var hidden = tr.getChildrenByClassName('idReserva')[0];
	var text = tr.getChildrenByClassName('vagasReserva')[0];
	
	var valorText = 0
	if( text != null )
		valorText = text.dom.value;
	
	getEl('idReservaCurso').dom.value = hidden.dom.value;
	getEl('qtdVagas').dom.value = valorText;
}
</script>

<style>
	table.subFormulario tr.turmaAgrupadora td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	.left { text-align: left; }
	.rigth { text-align: right; }
	.center { text-align: center; }
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>


<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Cadastro de Turmas &gt; Dados Gerais </h2>

	<c:if test="${turmaLatoSensuBean.obj.disciplina.aceitaSubturma }">
		<div class="descricaoOperacao">
			<p>Caro Usuário,</p>
			<p><b>Atenção! Este é um componente que permite a criação de subturmas. Porém, para que isso aconteça, é
			 necessário que as turmas criadas tenham algum horário em comum. </b></p>
		</div>
		<br/>
	</c:if>

	<h:form id="cadastroTurma">
	
	<table class="formulario" style="width: 100%">
		<caption class="formulario">Dados da Turma </caption>
		<tr>
			<td colspan="2" class="subFormulario">Dados do Componente Curricular</td>
		</tr>
		<tr>
			<th width="40%"><b>Componente Curricular:</b></th>
			<td>
				<h:outputText value="#{turmaLatoSensuBean.obj.disciplina.codigo}" /> - <h:outputText value="#{turmaLatoSensuBean.obj.disciplina.detalhes.nome}" />
			</td>
		</tr>
		<tr>
			<th><b>Tipo do Componente:</b></th>
			<td><h:outputText value="#{turmaLatoSensuBean.obj.disciplina.tipoComponente.descricao}" /></td>
		</tr>
		<c:if test="${turmaLatoSensuBean.obj.disciplina.atividade}">
			<tr>
				<th><b>Tipo de Atividade:</b></th>
				<td><h:outputText value="#{turmaLatoSensuBean.obj.disciplina.tipoAtividade.descricao}" /></td>
			</tr>
		</c:if>
		<tr>
			<th><b>CH Total:</b></th>
			<td><h:outputText value="#{turmaLatoSensuBean.obj.disciplina.detalhes.chTotal}" /> h</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">Dados Gerais da Turma</td>
		</tr>
		<tr>
			<th><b>Tipo da Turma:</b></th>
			<td>
				<h:outputText value="#{ turmaLatoSensuBean.obj.tipoString }"  />
			</td>
		</tr>
		<tr>
			<th><b>Modalidade:</b></th>
			<td>
				<h:outputText id="modalidadeFormatado" value="#{turmaLatoSensuBean.modalidadeTurma}" />
			</td>
		</tr>
		<c:if test="${not turmaLatoSensuBean.selecionaCurso}">
			<tr>
				<th><b>Curso:</b></th>
				<td>
					<h:outputText value="#{turmaLatoSensuBean.obj.curso.descricao}"/>
				</td>
			</tr>
		</c:if>
 		<c:if test="${turmaLatoSensuBean.selecionaCurso}">
			<tr>
				<th class="required">Curso:</th>
				<td>
					<h:selectOneMenu value="#{turmaLatoSensuBean.obj.curso.id}" id="selectCurso" valueChangeListener="#{turmaLatoSensuBean.reCarregarCurso}" onchange="submit()">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{turmaLatoSensuBean.cursosCombo}" />
						<a4j:support reRender="modalidadeFormatado, painelCodigoTurma, painelCapacidadeAlunos"></a4j:support>
					</h:selectOneMenu>
				</td>
			</tr>
 		</c:if>
		<tr>
			<th class="required">Ano-Período:</th>
			<td>
				<c:choose>
					<c:when test="${ turmaLatoSensuBean.obj.id == 0}">
						<a4j:region>
							<h:inputText value="#{turmaLatoSensuBean.obj.ano}" maxlength="4" size="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }">
								<a4j:support event="onblur" reRender="painelSubTurmas, dataInicioTurma, dataFimTurma" actionListener="#{turmaLatoSensuBean.atualizarDataInicioFim}" />
							</h:inputText>
							-
							<h:inputText value="#{turmaLatoSensuBean.obj.periodo}" maxlength="1" size="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }">
								<a4j:support event="onblur" reRender="painelSubTurmas, dataInicioTurma, dataFimTurma" actionListener="#{turmaLatoSensuBean.atualizarDataInicioFim}" />
							</h:inputText>
							
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
							
						</a4j:region>
					</c:when>
					<c:otherwise>
						<h:outputText value="#{turmaLatoSensuBean.obj.ano}" />-<h:outputText value="#{turmaLatoSensuBean.obj.periodo}" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:if test="${turmaLatoSensuBean.obj.campusObrigatorio || acesso.dae}">
			<tr>
			<th class="${ (turmaLatoSensuBean.obj.campusObrigatorio && turmaLatoSensuBean.obj.aberta) ? 'required' : '' }">Campus:</th>
			<td>
				<h:selectOneMenu id="campus" value="#{turmaLatoSensuBean.obj.campus.id}" disabled="#{!turmaLatoSensuBean.obj.aberta}">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- "  />
					<f:selectItems value="#{campusIes.allCampusCombo}"/>
				</h:selectOneMenu>
			</td>
			</tr>
		</c:if>
		<a4j:outputPanel id="painelCodigoTurma" rendered="#{turmaLatoSensuBean.editarCodigoTurma}">
			<tr>
				<th class="required">Código da Turma: </th>
				<td>
					<h:inputText id="codigo" value="#{ turmaLatoSensuBean.obj.codigo }" size="4" disabled="#{(!turmaLatoSensuBean.passivelEdicao || turmaLatoSensuBean.matriculada) && !turmaLatoSensuBean.turmaEad}"/>
				</td>
			</tr>
		</a4j:outputPanel>
		<a4j:outputPanel id="painelCapacidadeAlunos" rendered="#{!turmaLatoSensuBean.turmaEad}">
			<tr>
				<th class="required">Capacidade de Alunos:</th>
				<td>
					<h:inputText id="vagas" value="#{ turmaLatoSensuBean.obj.capacidadeAluno }"  size="4" maxlength="3" 
					disabled="#{!(turmaLatoSensuBean.passivelEdicao || turmaLatoSensuBean.adicionarOutrasReservas)}"  
					onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>
			<tr>
				<th class="required">Local:</th>
				<td>
					<h:inputText id="local" value="#{ turmaLatoSensuBean.obj.local }" maxlength="50" size="50" disabled="#{!turmaLatoSensuBean.obj.aberta}"/>
				</td>
			</tr>
			<c:if test="${turmaLatoSensuBean.turmaStricto}">
				<tr>
					<th>Observações ao Aluno: 
					<ufrn:help img="/img/ajuda.gif" width="320">Utilize este campo para colocar observações para o aluno realizar a matricula
					Por exemplo: nas disciplinas de tópicos especiais pode ser adicionado a informação de qual será o assunto abordado, estas observações aparecerão no histórico ao lado do nome da disciplina.</ufrn:help>
					<br/>
					<span style="font-size: xx-small"></span>
					</th>
					<td>
						<h:inputTextarea id="observacoes" value="#{ turmaLatoSensuBean.obj.observacao }" rows="2" cols="50" disabled="#{!turmaLatoSensuBean.obj.aberta && turmaLatoSensuBean.portalCoordenadorStricto}"/>
					</td>
				</tr>
			</c:if> 
		</a4j:outputPanel>
		<tr>
			<th class="required">Início:</th>
			<td>
				<t:inputCalendar value="#{turmaLatoSensuBean.obj.dataInicio}"  renderAsPopup="true" size="10" maxlength="10" id="dataInicioTurma"
				popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)" popupDateFormat="dd/MM/yyyy">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required">Fim:<br/>
			</th>
			<td>
				<t:inputCalendar value="#{turmaLatoSensuBean.obj.dataFim}"  renderAsPopup="true" size="10" maxlength="10" id="dataFimTurma"
				popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)" popupDateFormat="dd/MM/yyyy">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="2">
					<c:if test="${turmaLatoSensuBean.obj.id == 0}">
						<h:commandButton value="<< Selecionar Outro Componente" action="#{ turmaLatoSensuBean.formSelecaoComponente }" id="voltar"/>
					</c:if>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaLatoSensuBean.cancelar }" id="cancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{ turmaLatoSensuBean.submeterDadosGerais }" id="proximoPasso"/>
				</td>
			</tr>
		</tfoot>
		</table>
	</h:form>
	
	<br>
	<center><h:graphicImage url="/img/required.gif" style="vertical-align: top;" /><span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>