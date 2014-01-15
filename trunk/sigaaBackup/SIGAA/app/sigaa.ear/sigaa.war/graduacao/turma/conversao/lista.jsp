<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2><ufrn:subSistema /> &gt; Converter Turma Regular em Ensino Individual</h2>
<h:form>
	<a4j:keepAlive beanName="converterTurmaRegularIndividualMBean"></a4j:keepAlive>
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Este formulário permite listar as turmas regulares que possuem o número máximo de discentes matriculados necessários para a criação 
		de uma turma de ensino individual podendo, assim, serem convertidas de regular para turma de ensino individual.</p>
	</div>
	</br>
	<table class="formulario" width="65%">
	<caption>Busca por Turma</caption>
	<tbody>
		<tr>
			<th width="40%" class="obrigatorio">Ano-Período:</th>
			<td>
				<h:inputText value="#{converterTurmaRegularIndividualMBean.obj.ano}" maxlength="4" size="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
				-
				<h:inputText value="#{converterTurmaRegularIndividualMBean.obj.periodo}" maxlength="1" size="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio"> Quantidade de Discentes Matriculados:</th>
			<td> 
				<h:inputText value="#{converterTurmaRegularIndividualMBean.qtdMatriculado}" maxlength="1" size="1" id="qtdMatriculado" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/> 
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Buscar" action="#{ converterTurmaRegularIndividualMBean.listarTurmasComPoucosDiscentes }" id="btBuscar"/>
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ converterTurmaRegularIndividualMBean.cancelar }" id="btCancelar"/>
			</td>
		</tr>
	</tfoot>
	</table>
	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	<c:if test="${ not empty converterTurmaRegularIndividualMBean.resultadosBusca }">
		<br/>
		<div class="infoAltRem">
			<h:graphicImage url="/img/seta.gif"  alt="Converter Turma" title="Converter Turma" />: Converter Turma
		</div>
		<c:set var="idUnidade" value="0"/>
		<c:set var="parImpar" value="0"/>
		<table class="listagem" width="100%">
			<caption>Turmas Encontradas (${ fn:length(converterTurmaRegularIndividualMBean.resultadosBusca) })</caption>
			<thead>
				<tr>
					<th colspan="2" style="text-align: left;">Componente Curricular</th>
					<th width="5%" style="text-align: right;">Turma</th>
					<th width="5%" style="text-align: right;">Matriculados/<br/>Capacidade</th>
					<th width="15%" style="text-align: left;">Situação</th>
					<th width="5%"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{ converterTurmaRegularIndividualMBean.resultadosBusca }" var="item" varStatus="status">
				<c:if test="${ idUnidade != item.disciplina.unidade.id }">
					<c:set var="idUnidade" value="${ item.disciplina.unidade.id }"/>
					<tr>
						<td colspan="7" class="subFormulario">${ item.disciplina.unidade.nome }</td>
						<c:set var="parImpar" value="0"/>
					</tr>
				</c:if>
				<tr class="${ parImpar % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align: right;">${ item.disciplina.codigo }</td>
					<td style="text-align: left;">${ item.disciplina.nome }</td>
					<td style="text-align: right;">${ item.codigo }</td>
					<td style="text-align: right;">${ item.qtdMatriculados }/${ item.capacidadeAluno }</td>
					<td style="text-align: left;">${ item.situacaoTurma.descricao }</td>
					<td style="text-align: right;">
						<h:commandLink action="#{ converterTurmaRegularIndividualMBean.selecionarTurma }">
							<h:graphicImage url="/img/seta.gif"  alt="Converter Turma" title="Converter Turma" />
							<f:param name="id" value="#{ item.id }" />
						</h:commandLink>
					</td>
				</tr>
				<c:set var="parImpar" value="${ parImpar + 1 }"/>
				</c:forEach>
			</tbody>
			<tfoot>
		<tr>
			<td colspan="7" style="text-align: center;">
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ converterTurmaRegularIndividualMBean.cancelar }" id="btCancelar2"/>
			</td>
		</tr>
	</tfoot>
		</table>
	</c:if>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
