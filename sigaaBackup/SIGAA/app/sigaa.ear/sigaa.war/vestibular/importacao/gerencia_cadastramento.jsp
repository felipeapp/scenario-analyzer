<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="cadastramentoDiscente"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Gerenciar Cadastramento de Discentes</h2>
<div class="descricaoOperacao">
	<p> Esta operação permite confirmar os alunos que compareceram ao cadastramento após uma importação de discentes aprovados em outros concursos,
	bem como identificar aqueles que não compareceram, excluindo suas matrículas do sistema. </p>
	<ul>
		<li>Para indicar que um aluno <strong>não compareceu</strong> ao cadastramento <strong>desmarque a caixinha</strong> correspondente ao aluno. </li>
	</ul>
	<p> Também nesta operação é possível cancelar as convocações dos alunos já cadastrados mas que tiveram seus vínculos cancelados no sistema pelo não comparecimento à matrícula. </p>
</div>

<h:form>

<table class="formulario" width="80%">
	<caption>Parâmetros do Cadastramento</caption>
	<tr>
		<th class="obrigatorio">Processo de Importação: </th>
		<td width="70%" style="padding-left: 5px;">
			<h:selectOneMenu id="processoSeletivo" immediate="true"
				value="#{cadastramentoDiscente.importacaoDiscente.id}"
				valueChangeListener="#{documentosDiscentesImportadosMBean.carregarMatrizes}"
				onchange="submit()" >
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{documentosDiscentesImportadosMBean.allImportacaoCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Matriz Curricular: </th>
		<td width="70%;">
				<h:selectOneMenu id="curso" value="#{cadastramentoDiscente.matriz.id}" 
					rendered="#{not empty documentosDiscentesImportadosMBean.matrizesCombo}" style="max-width: 850px !important;">
					<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
					<f:selectItems value="#{documentosDiscentesImportadosMBean.matrizesCombo}"/>
				</h:selectOneMenu>
				<h:outputText value="Não há matrizes curriculares importadas para o processo de importação selecionado." rendered="#{empty documentosDiscentesImportadosMBean.matrizesCombo}" />
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnBuscar" value="Buscar Discentes" action="#{cadastramentoDiscente.buscarDiscentesImportados}"/>
				<h:commandButton value="Cancelar" action="#{ cadastramentoDiscente.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>

<br/><br/>

<c:if test="${ not empty cadastramentoDiscente.discentes }">
<c:set var="habilitaProximoPasso" value="false"/>
<table class="listagem">
	<caption>Discentes encontrados (${fn:length(cadastramentoDiscente.discentes)})</caption>
	<thead>
		<tr>
			<th>Compareceu</th>
			<th style="text-align: center;">Ingresso</th>
			<th style="text-align: center;">Matrícula</th>
			<th>Nome</th>
			<th>Status</th>
		</tr>
	</thead>
	<c:forEach items="#{cadastramentoDiscente.discentes}" var="item" varStatus="loop">
		<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td>
				<h:selectOneMenu id="selectCompareceu" value="#{item.opcaoCadastramento}"  
					rendered="#{(item.discente.pendenteCadastro) && conv.cancelamento == null}" 
					title="#{item.pessoa.nome}">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue=""/>
					<f:selectItems value="#{cadastramentoDiscente.opcoesCadastramentoCombo}"/>
				</h:selectOneMenu>
				<c:set var="habilitaProximoPasso" value="${habilitaProximoPasso || item.discente.pendenteCadastro || item.discente.cancelado}" />
			</td>
			<td style="text-align: center;"><h:outputText value="#{item.anoPeriodoIngresso}" /></td>
			<td style="text-align: center;"><h:outputText value="#{item.matricula}" /></td>
			<td><h:outputText value="#{item.pessoa.nome}"/></td>
			<td><h:outputText value="#{item.statusString}"/></td>
		</tr>
	</c:forEach>
	<c:if test="${habilitaProximoPasso}">	
		<tfoot>
		<tr>
			<td colspan="6" style="text-align: center;">
			<h:commandButton value="Processar Cadastramento >>" action="#{ cadastramentoDiscente.processarDiscentesImportados }" id="btnProcessar"/>
			</td>
		</tr>
		</tfoot>
	</c:if>
</table>
</c:if>

</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>