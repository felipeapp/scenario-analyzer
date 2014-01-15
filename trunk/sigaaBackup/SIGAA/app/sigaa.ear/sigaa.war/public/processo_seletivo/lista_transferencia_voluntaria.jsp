<%@include file="/public/include/cabecalho.jsp"%>

<style>
.periodo {
	font-weight: bold;
	color: #292;
}

.fechado {
	color: #555;
}

.nivel {
	text-transform: uppercase;
}

#consulta {
	color: #292;
}

#consulta th,#consulta td {
	padding: 6px;
}

#consulta th {
	font-weight: bold;
}

tr td.agrupador{
	height:10px;
	font-weight: bold;
	background-color: #EDF1F8;
	text-transform: uppercase;
	border-top: 1px solid #DAE5F8;
}
.linhaImpar{background-color: #eee;}
.colData{
	text-align: center !important;
}
</style>

<f:view>
	<h:outputText value="#{processoSeletivo.create}" />
	<h:outputText value="#{inscricaoSelecao.create}" />

	<h2>Processos Seletivos de Tranfer�ncia Volunt�ria - ${processoSeletivo.nivelDescricao}</h2>

	<h:form id="form">

		<div class="descricaoOperacao">
		<p><b>Caro visitante,</b></p>
		<p>
		Nesta p�gina voc� poder� se inscrever no processo seletivo para transfer�ncia volunt�ria.
		Caso o per�odo de inscri��es esteja aberto, preencher o formul�rio
		destinado para tal .</p>
		<p>Ser� poss�vel visualizar as informa��es destes processos, como
		o curso a que ele se refere, o per�odo de inscri��es, alguns arquivos
		associados (como editais e manuais) e as instru��es aos candidatos.</p>
		<p>Para cada processo listado est� tamb�m dispon�vel um <b><i>
		formul�rio de inscri��o</i></b> para os candidatos.</p>
		<p>Os per�odos dos processos seletivos marcados na cor <b>verde</b> est�o em aberto.</p>
		<p>O resultado da consulta realizada pelo CPF retorna todos os processos seletivos nos quais a pessoa est� inscrita.</p>
		</div>

		<table class="formulario" id="consulta">
			<tr>
				<th>Consultar inscri��es do CPF:</th>
				<td><h:inputText
					value="#{inscricaoSelecao.obj.pessoaInscricao.cpf}" maxlength="14" size="15"
					onkeypress="return formataCPF(this, event, null)" id="txtCPF"
					style="padding: 2px; font-size: 1.1em;">
					<f:converter converterId="convertCpf" />
				</h:inputText></td>
				<td>
				<h:commandButton value="Buscar" actionListener="#{inscricaoSelecao.buscarInscricoes}">
					<f:attribute name="nivel" value="#{processoSeletivo.nivel}" />
				</h:commandButton>
				</td>
			</tr>
		</table>

		<c:set var="processosSeletivos"
			value="#{processoSeletivo.allVisiveis}" />

		<center><h:messages />
	<div class="infoAltRem"><br/><h:graphicImage value="/img/seta.gif"
		style="vertical-align: middle; overflow: visible;" />:Selecionar dados do processo seletivo<br />
	</div>
	</center>

		<c:if test="${not empty processosSeletivos}">
			<br />
			<table class="listagem">
				<caption class="listagem">�ltimos Processos Seletivos</caption>
					 <c:set var="_edital" value=""/>
					<c:forEach items="#{processosSeletivos}" var="item"
						varStatus="status">
						
						<%-- SE PRIMEIRO LOOP IMPRIME O CABE�ALHO --%>
						<c:if test="${status.first}">
							<thead>
								<tr>
									<th><b>Curso</b></th>
									<c:choose>
										<%-- SE PROCESSO SELETIVO CURSO LATOS, P�S E T�CNICO --%>
										<c:when test="${not empty item.curso}">
											<th><b>N�vel</b></th>
										</c:when>
										<%-- SE PROCESSO SELETIVO CURSO GRADUAC�O --%>
										<c:otherwise>
											<th><b>�rea</b></th>
											<th><b>Cidade</b></th>
											<th><b>N� Vagas</b></th>
										</c:otherwise>	
									</c:choose>
									<th class="colData"><b>Per�odo de Inscri��es</b></th>
									<th colspan="2"></th>
								</tr>
							</thead>
							<tbody>
						</c:if>
						
						<%-- SE PROCESSO SELETIVO CURSO GRADUAC�O, AGRUPAR POR �REA  --%>
						<c:if test="${not empty item.editalProcessoSeletivo && _edital != item.editalProcessoSeletivo.id}">
							<c:if test="${!status.first}">
							<tr>
								<td colspan="7" height="5px">
								</td>
							</tr>
							</c:if>
							<tr>
								<td colspan="4" class="agrupador">
								${item.editalProcessoSeletivo.nome}
								</td>
								<td colspan="3" class="agrupador" >
								<ufrn:format type="data" valor="${item.editalProcessoSeletivo.inicioInscricoes}" /> 
								� <ufrn:format type="data" valor="${item.editalProcessoSeletivo.fimInscricoes}" />
								</td>
							</tr>
						 </c:if>
						 <c:set var="_edital" value="${item.editalProcessoSeletivo.id}"/>
							
					

						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<c:choose>
								<%-- SE PROCESSO SELETIVO CURSO LATOS, P�S E T�CNICO --%>
								<c:when test="${not empty item.curso}">
									<td>${item.curso.descricao}</td>
									<td class="nivel">${item.curso.nivelDescricao}</td>
								</c:when>
								<%-- SE PROCESSO SELETIVO CURSO GRADUAC�O --%>
								<c:otherwise>
									<td>${item.matrizCurricular.descricao}
									</td>
									<td>${item.matrizCurricular.curso.areaVestibular.descricao}</td>
									<td>
									${item.matrizCurricular.curso.municipio.nome}
									</td>
									<td align="center">${item.vaga}</td>
								</c:otherwise>
							</c:choose>									
							<td nowrap="nowrap" class="colData periodo ${!item.inscricoesAbertas ? 'fechado' : ''} }">
								<ufrn:format type="data" valor="${item.editalProcessoSeletivo.inicioInscricoes}" /> 
								a <ufrn:format type="data" valor="${item.editalProcessoSeletivo.fimInscricoes}" />
							</td>
							<td width="16">
								<h:commandLink title="Selecionar dados do processo seletivo" action="#{processoSeletivo.viewPublico}">
									<h:graphicImage url="/img/seta.gif" />
									<f:param name="id" value="#{item.id}" />
								</h:commandLink>	
							</td>
						</tr>
						<%-- SE PROCESSO SELETIVO CURSO GRADUAC�O --%>
						<c:if test="${not empty item.matrizCurricular}">
							<c:set var="ultimaArea" value="${item.matrizCurricular.curso.areaVestibular.descricao}"/>
						</c:if>
					</c:forEach>
			</table>
		</c:if>

		<c:if test="${empty processosSeletivos}">
			<style>
			.aviso {
				margin: 10px 20% 0;
				padding: 8px 10px;
				border: 1px solid #AAA;
				background: #EEE;
				color: #922;
				text-align: center;
				fon
			}
			</style>
		
				<div class="aviso">
					<p>Nenhum processo seletivo encontra-se aberto para inscri��es
					neste n�vel de ensino.</p>
					</div>
		</c:if>

	</h:form>
</f:view>

<%@include file="/public/include/voltar.jsp"%>
<%@include file="/public/include/rodape.jsp"%>