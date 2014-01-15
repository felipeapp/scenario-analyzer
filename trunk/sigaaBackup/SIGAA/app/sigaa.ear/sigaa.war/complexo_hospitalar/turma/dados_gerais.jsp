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
	
	.bold{ .required; font-weight: bold; }
</style>


<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Cadastro de Disciplinas &gt; Dados Gerais </h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Nesta tela deve-se entrar com os dados gerais da turma.
			As datas de início e fim das disciplinas não podem ser alteradas e são preenchidas automaticamente com as datas de início e fim do ano letivo. 	
		</p>
	</div>

	<h:form id="cadastroTurma">
	
	<br/>
	<table class="formulario" style="width: 100%">
		<caption class="formulario">Dados da Disciplina </caption>
		<tr>
			<td colspan="2" class="subFormulario">Dados do Componente Curricular</td>
		</tr>
		<tr>
			<th width="40%"><b>Componente Curricular:</b></th>
			<td>
				<h:outputText value="#{turmaResidenciaMedica.obj.disciplina.codigo}" /> - <h:outputText value="#{turmaResidenciaMedica.obj.disciplina.detalhes.nome}" />
			</td>
		</tr>
		<tr>
			<th><b>Tipo do Componente:</b></th>
			<td><h:outputText value="#{turmaResidenciaMedica.obj.disciplina.tipoComponente.descricao}" /></td>
		</tr>
		<c:if test="${turmaResidenciaMedica.obj.disciplina.atividade}">
			<tr>
				<th><b>Tipo de Atividade:</b></th>
				<td><h:outputText value="#{turmaResidenciaMedica.obj.disciplina.tipoAtividade.descricao}" /></td>
			</tr>
		</c:if>
		<c:if test="${(turmaResidenciaMedica.obj.disciplina.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva) and turmaResidenciaMedica.obj.disciplina.detalhes.crTotal > 0}">
			<tr>
				<th><b>Cr Total:</th>
				<td><h:outputText value="#{turmaResidenciaMedica.obj.disciplina.detalhes.crTotal}" /> crs. (${turmaResidenciaMedica.obj.disciplina.detalhes.chTotal} h)</td>
			</tr>
		</c:if>
		<c:if test="${not (componenteCurricular.obj.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva)}">
			<tr>
				<th><b>CH Total:</b></th>
				<td><h:outputText value="#{turmaResidenciaMedica.obj.disciplina.detalhes.chTotal}" /> h</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" class="subFormulario">Dados Gerais da Disciplina</td>
		</tr>
		<tr>
			<th><b>Tipo da Turma:</b></th>
			<td>
				<h:outputText value="#{ turmaResidenciaMedica.obj.tipoString }"  />
			</td>
		</tr>
		<tr>
			<th><b>Modalidade:</b></th>
			<td>
				<c:choose>
					<c:when test="${ !turmaResidenciaMedica.turmaEad }">Presencial</c:when>
					<c:otherwise>A Distância</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:if test="${especializacaoTurma.utilizadoPelaGestora && not empty especializacaoTurma.especializacoesUnidadeGestoraCombo}">
			<tr>
				<th>Especialidade da Turma de Entrada: </th>
				<td>
					<h:selectOneMenu id="especializacaoEntrada" value="#{turmaResidenciaMedica.obj.especializacao.id}">
						<f:selectItem itemValue="0" itemLabel="-- TODAS --"/>
						<f:selectItems value="#{especializacaoTurma.especializacoesUnidadeGestoraCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</c:if>
		<tr>
			<th class="${ turmaResidenciaMedica.obj.id == 0 ? 'required' : 'bold'}"  ">Ano:</th>
			<td>
				<c:choose>
					<c:when test="${ turmaResidenciaMedica.obj.id == 0}">
						<a4j:region>
							<h:inputText value="#{turmaResidenciaMedica.obj.ano}" maxlength="4" size="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }">
								<a4j:support event="onblur" reRender="painelSubTurmas, dataInicioTurma, dataFimTurma" actionListener="#{turmaResidenciaMedica.atualizarDataInicioFim}" />
							</h:inputText>
							
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
							
						</a4j:region>
					</c:when>
					<c:otherwise>
						<h:outputText value="#{turmaResidenciaMedica.obj.ano}" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		
		<c:if test="${ turmaResidenciaMedica.editarCodigoTurma}">
			<tr>
				<th class="required">Código da Turma: </th>
				<td>
					<h:inputText id="codigo" value="#{ turmaResidenciaMedica.obj.codigo }" size="4" disabled="true"/>
				</td>
			</tr>
		</c:if>
		<tr>
			<th class="required">Capacidade de Alunos:</th>
			<td>
				<h:inputText id="vagas" value="#{ turmaResidenciaMedica.obj.capacidadeAluno }"  size="4" maxlength="3" 
				disabled="#{!(turmaResidenciaMedica.passivelEdicao || turmaResidenciaMedica.adicionarOutrasReservas)}"  
				onkeyup="return formatarInteiro(this);"/>
			</td>
		</tr>
		<tr>
			<th class="required">Local:</th>
			<td>
				<h:inputText id="local" value="#{ turmaResidenciaMedica.obj.local }" maxlength="50" size="50" disabled="#{!turmaResidenciaMedica.obj.aberta}"/>
			</td>
		</tr>
		<tr>
			<th>Observações ao Aluno: 
			<ufrn:help img="/img/ajuda.gif" width="320">
				Utilize este campo para colocar observações para o aluno realizar a matrícula.
				Por exemplo: nas disciplinas de tópicos especiais pode ser adicionado a informação de qual será o assunto abordado, 
				estas observações aparecerão no histórico ao lado do nome da disciplina.
			</ufrn:help>
			<br/>
			<span style="font-size: xx-small"></span>
			</th>
			<td>
				<h:inputTextarea id="observacoes" value="#{ turmaResidenciaMedica.obj.observacao }" rows="2" cols="50" disabled="#{!turmaResidenciaMedica.obj.aberta}"/>
			</td>
		</tr>
		<tr>
			<th class="required">Início:</th>
			<td>
				<t:inputCalendar value="#{turmaResidenciaMedica.obj.dataInicio}"  renderAsPopup="true" size="10" maxlength="10" id="dataInicioTurma"
				popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
				popupDateFormat="dd/MM/yyyy" disabled="#{ !turmaResidenciaMedica.passivelEdicao }">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required">Fim:<br/>
			</th>
			<td>
				<t:inputCalendar value="#{turmaResidenciaMedica.obj.dataFim}"  renderAsPopup="true" size="10" maxlength="10" id="dataFimTurma"
				popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
				popupDateFormat="dd/MM/yyyy" disabled="#{ !turmaResidenciaMedica.passivelEdicao }">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Voltar" action="#{ turmaResidenciaMedica.formSelecaoComponente }" id="back"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaResidenciaMedica.cancelar }" id="cancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{ turmaResidenciaMedica.submeterDadosGerais }" id="proximoPasso"/>
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