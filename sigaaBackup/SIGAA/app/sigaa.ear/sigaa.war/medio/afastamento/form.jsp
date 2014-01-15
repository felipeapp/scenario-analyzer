<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="afastamentoDiscenteMedioMBean"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> &gt; <h:outputText value="#{(afastamentoDiscenteMedioMBean.conclusao ?'Conclusão de Programa':'Cadastrar Afastamento')}"/></h2>

	<div class="descricaoOperacao">
		<p> <strong>Atenção!</strong> </p>
		<p>O conteúdo inserido no campo <strong>Observação</strong> do formulário abaixo será exibido no histórico do aluno.</p>
	</div>

	<c:set var="discente" value="#{afastamentoDiscenteMedioMBean.obj.discente}" />
	<%@include file="/medio/discente/info_discente.jsp"%>

	<h:form id="form">
	<table class="formulario" width="100%">
		<caption>Informe os dados para <h:outputText value="#{(afastamentoDiscenteMedioMBean.conclusao?'a Conclusão':'o Afastamento') }"/></caption>
			<c:if test="${afastamentoDiscenteMedioMBean.conclusao}">
				<tr>
					<th width="25%" class="required">Data de Colação:</th>
					<td>
						<t:inputCalendar id="dataColacaoGrau" value="#{afastamentoDiscenteMedioMBean.obj.dataColacaoGrau}" 
							renderAsPopup="true" size="10" maxlength="10" popupTodayString="Hoje" 
							renderPopupButtonAsImage="true" onkeypress="formataData(this,event)" popupDateFormat="dd/MM/yyyy"> 
						<f:converter converterId="convertData"/> 
						</t:inputCalendar>
					</td>
				</tr>
			</c:if>
			<c:if test="${not afastamentoDiscenteMedioMBean.conclusao}">
				<tr>
					<th width="25%" class="required">Tipo:</th>
					<td><h:selectOneMenu value="#{afastamentoDiscenteMedioMBean.obj.tipoMovimentacaoAluno.id}" id="tipo">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{afastamentoDiscenteMedioMBean.tiposAfastamentos}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="required" style="width: 20%">Ano de Referência:</th>
				<td><h:inputText id="ano" value="#{afastamentoDiscenteMedioMBean.obj.anoReferencia}" size="4" maxlength="4" 
							onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
				</td>
			</tr>
			<tr>
				<th valign="top">Observação:</th>
				<td><h:inputTextarea id="observacao" rows="3" value="#{afastamentoDiscenteMedioMBean.obj.observacao}" style="width: 95%;">
					</h:inputTextarea></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="<< Escolher Outro Discente" id="voltarConclusao" action="#{afastamentoDiscenteMedioMBean.telaBuscaDiscentes}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{afastamentoDiscenteMedioMBean.cancelar}" />
						<h:commandButton value="Próximo Passo >>" id="proximo" action="#{afastamentoDiscenteMedioMBean.submeterDadosAfastamento}" />
					</td>
				</tr>
			</tfoot>
	</table>
	<br />

	
	</h:form>

	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br /><br />
	</center>
	
	<c:if test="${not empty afastamentoDiscenteMedioMBean.historicoMovimentacoes}">
	<table class="subFormulario" style="width: 100%">
		<caption>Histórico de Movimentações do Discente</caption>
		<thead>
			<tr>
				<td width="8%"></td>
				<td>Tipo</td>
				<td>Usuário</td>
				<td width="15%">Data</td>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${afastamentoDiscenteMedioMBean.historicoMovimentacoes}" var="mov" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td>${mov.anoPeriodoReferencia }</td>
				<td>${mov.tipoMovimentacaoAluno.descricao}</td>
				<td>${mov.usuarioCadastro.pessoa.nome}</td>
				<td><ufrn:format type="dataHora" valor="${mov.dataOcorrencia}" /></td>
			</tr>
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td colspan="4"><i>${mov.observacao}</i></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
