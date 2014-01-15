<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<f:view>

<style>
	tr.componente td{
		background: #C4D2EB;
		font-weight: bold;
		border-bottom: 1px solid #BBB;
		color: #222;
	}
</style>

	<h2> <ufrn:subSistema /> &gt; Transferência entre Turmas ${transferenciaTurmaMedioMBean.descricaoTipo}  &gt; Definir Turma de Origem</h2>

	<div class="descricaoOperacao">
		<p> <strong>Caro Usuário,</strong> </p>

		<p>
			Esta operação destina-se a efetuar a transferências de matrículas de discentes entre turmas:
		</p>
		<ol>
			<li> Inicialmente selecione o curso e série desejados;</li> 
			<li> Em seguida selecione uma das turmas para que seja a origem da transferência;</li> 
			<li> Na tela seguinte selecione uma das turmas para que seja o destino da transferência;</li>
			<c:choose>
				<c:when test="${transferenciaTurma.automatica}">
					<li> Informe o número de alunos a serem transferidos, respeitando os limites das turmas.</li>
				</c:when>
				<c:otherwise>
					<li> Selecione os discentes a serem transferidos, respeitando os limites das turmas.</li>
				</c:otherwise>			
			</c:choose>
		</ol>
	</div>

	<h:form id="buscaTurmaOrigem">
	<table class="formulario" width="95%">
		<caption class="formulario">Buscar Turma de Origem</caption>
		<tbody>
			<tr>
				<th>Curso:</th>
				<td>
				<a4j:region>
					<h:selectOneMenu value="#{transferenciaTurmaMedioMBean.serie.cursoMedio.id}" id="selectCurso"
						valueChangeListener="#{transferenciaTurmaMedioMBean.carregarSeriesByCurso }" style="width: 95%">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
				 		<a4j:support event="onchange" reRender="selectSerie" />
					</h:selectOneMenu>
					<a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>
			<tr>
				<th>Série:</th>
				<td>
					<h:selectOneMenu value="#{ transferenciaTurmaMedioMBean.serie.id }" style="width: 95%;" id="selectSerie">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ transferenciaTurmaMedioMBean.series }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano:</th>
				<td>
					<h:inputText value="#{transferenciaTurmaMedioMBean.ano}" size="4" maxlength="4" 
					onkeyup="return formatarInteiro(this);"/> 
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Buscar" actionListener="#{transferenciaTurmaMedioMBean.buscarTurmasSerie}" id="btnBuscar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{transferenciaTurmaMedioMBean.cancelar}" id="btnCancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

	</h:form>

	<c:if test="${not empty transferenciaTurmaMedioMBean.turmasOrigem}">
		<br>
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Turma
		</div>

		<h:form id="listaTurmas">
		<table class=listagem>
			<caption class="listagem">Lista de Turmas Encontradas</caption>
			<thead>
			<tr>
				<td width="50%">Turma</td>
				<td align="center">Ano</td>
				<td >Turno</td>
				<td style="text-align: right;">Matriculados</td>
				<td style="text-align: right;">Capacidade</td>
				<td></td>
			</tr>
			</thead>

			<c:set var="componente" />
			<c:forEach items="#{transferenciaTurmaMedioMBean.turmasOrigem}" var="item" varStatus="linha">
				<tr class="${linha.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td> ${item.serie.descricaoCompleta} - ${item.dependencia ?'Dependência':item.nome} </td>
					<td> ${item.ano} </td>
					<td> ${item.turno.descricao}</td>
					<td align="right"> ${ item.qtdMatriculados } </td>
					<td align="right"> ${ item.capacidadeAluno } </td>
					<td width="2%">
						<h:commandLink id="selecionarTurma" action="#{transferenciaTurmaMedioMBean.selecionarTurmaOrigem}">
							<h:graphicImage url="/img/seta.gif" alt="Selecionar Turma" title="Selecionar Turma"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>
		</h:form>
	</c:if>
<script type="text/javascript">
	function populaAnoPeriodo() {
		if ($('buscaTurmaOrigem:inputAno') != null)
			$('buscaTurmaOrigem:paramAno').value = $('buscaTurmaOrigem:inputAno').value;
		if ($('buscaTurmaOrigem:inputPeriodo') != null)
			$('buscaTurmaOrigem:paramPeriodo').value = $('buscaTurmaOrigem:inputPeriodo').value;
	}
	populaAnoPeriodo();
</script>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>