<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@taglib uri="/WEB-INF/tld/cewolf.tld" prefix="cewolf"%>
<style>
table.tabelaRelatorio tbody td {
	border-bottom: 1px solid black;
}
</style>

<f:view>

	<%@include file="/graduacao/discente/panel.jsp"%>

	<table id="identificacao">
		<tr>
			<th style="font-weight: bold">Período Letivo:</th>
			<td> ${ indiceAcademicoMBean.anoSemestre }</td>
			<th style="font-weight: bold">Nível:</th>
			<td> ${ indiceAcademicoMBean.discente.nivelDesc }</td>
		</tr>
		<tr>
			<th width="20%" style="font-weight: bold">Matrícula:</h>
			<td width="45%"> ${	indiceAcademicoMBean.discente.matricula }</td>
			<th width="10%" style="font-weight: bold">Vínculo:</th>
			<td> ${discenteGraduacaoMBean.obj.tipoString }</td>
		</tr>
		<tr>
			<th style="font-weight: bold">Nome:</th>
			<td colspan="3"> ${ indiceAcademicoMBean.discente.pessoa.nome }</td>
		</tr>

		<tr>
			<th style="font-weight: bold">Curso:</th>
			<td> ${ indiceAcademicoMBean.discente.curso.descricao } - ${	indiceAcademicoMBean.discente.matrizCurricular.turno.sigla }</td>
			<th style="font-weight: bold">Cidade:</th>
			<td> ${ indiceAcademicoMBean.discente.curso.municipio.nome }</td>
		</tr>
		<c:if
			test="${ not empty indiceAcademicoMBean.discente.matrizCurricular.habilitacao || not empty indiceAcademicoMBean.discente.matrizCurricular.grauAcademico}">
			<tr>
				<c:if
					test="${ not empty indiceAcademicoMBean.discente.matrizCurricular.habilitacao }">
					<th style="font-weight: bold">Habilitação:</th>
					<td> ${ indiceAcademicoMBean.discente.matrizCurricular.habilitacao }</td>
				</c:if>
				<c:if
					test="${ not empty indiceAcademicoMBean.discente.matrizCurricular.grauAcademico }">
					<th style="font-weight: bold">Formação:</th>
					<td> ${ indiceAcademico.discente.matrizCurricular.grauAcademico.descricao }</td>
				</c:if>
			</tr>
		</c:if>
		<c:if test="${indiceAcademicoMBean.discente.discenteEad}">
			<tr>
				<th style="font-weight: bold">Pólo:</th>
				<td> ${indiceAcademicoMBean.discente.polo.descricao}</td>
			</tr>
		</c:if>

	</table>

	<br />
	<h2 style="width: 100%">Índices Acadêmicos</h2>

	<table class="tabelaRelatorio" width="100%">
		<thead>
			<tr>
				<th align="left">Sigla</th>
				<th align="left">Índice</th>
				<th style="text-align: right">Valor</th>

			</tr>
		</thead>
		<tbody>
			<c:forEach var="indice"
				items="${ indiceAcademicoMBean.discente.discente.indices }">
				<tr>
					<td align="left">${ indice.indice.sigla }</td>
					<td align="left">${ indice.indice.nome }</td>
					<td style="text-align: right">${indice.valor}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<br />
	
	<c:if test="${indiceAcademicoMBean.possivelCalcularGraficoMediaConclusaoNormalizada}">
	
		<h2 style="width: 100%">Gráfico da Média de Conclusão Normalizada</h2>
	
		<jsp:useBean id="dados" scope="page"
			class="br.ufrn.sigaa.ensino.jsf.GraficoMcn" />
		<table width="100%">
			<tr>
				<td><cewolf:chart id="graficoMcn" type="xy" xaxislabel="Média de Conclusão"
					yaxislabel="Porcentagem">
					<cewolf:colorpaint color="#FFFFFF" />
					<cewolf:data>
						<cewolf:producer id="dados">
							<cewolf:param name="mc" value="${ indiceAcademicoMBean.mc }" />
							<cewolf:param name="maiorMc"
								value="${ indiceAcademicoMBean.maiorMc }" />
							<cewolf:param name="menorMc"
								value="${ indiceAcademicoMBean.menorMc }" />
							<cewolf:param name="mean" value="${ indiceAcademicoMBean.meanMc }" />
							<cewolf:param name="std" value="${ indiceAcademicoMBean.stdMc }" />
						</cewolf:producer>
					</cewolf:data>
					<cewolf:chartpostprocessor id="dados" />
				</cewolf:chart> <cewolf:img chartid="graficoMcn" renderer="/cewolf" width="400"
					height="250" /></td>
				<td>
				<table width="100%">
					<tr>
						<td><strong>Média das MCs:</strong></td>
						<td><fmt:formatNumber value="${ indiceAcademicoMBean.meanMc }" pattern="##0.0000" /></td>
					</tr>
					<tr>
						<td><strong>Desvio Padrão das MCs:</strong></td>
						<td><fmt:formatNumber value="${ indiceAcademicoMBean.stdMc }" pattern="##0.0000" /></td>
					</tr>
					<tr>
						<td><strong>Maior MC:</strong></td>
						<td><fmt:formatNumber value="${ indiceAcademicoMBean.maiorMc }"	pattern="##0.0000" /></td>
					</tr>
					<tr>
						<td><strong>Menor MC:</strong></td>
						<td><fmt:formatNumber value="${ indiceAcademicoMBean.menorMc }" pattern="##0.0000" /></td>
					</tr>
				</table>
				<br/>
					<center>
					Você está entre as <strong><em><fmt:formatNumber value="${ indiceAcademicoMBean.porcentagemMc }" pattern="##0.00" />%</em></strong>
					melhores MCs do seu curso.
					</center>
				<br/><br/>
					<center>*Obs.: Os valores normalizados levam em consideração apenas os alunos concluídos.</center>	
				</td>
			</tr>
		</table>
	
	</c:if>
	
	<c:if test="${ indiceAcademicoMBean.possivelCalcularGraficoIndiceEficienciaAcademicaNormalizado} ">
	
		<br />
		<h2 style="width: 100%">Gráfico do Índice de Eficiência Acadêmica Normalizado</h2>
	
		<jsp:useBean id="dadosIean" scope="page"
			class="br.ufrn.sigaa.ensino.jsf.GraficoIean" />
		<table width="100%">
			<tr>
				<td><cewolf:chart id="graficoIean" type="xy" xaxislabel="Índice de Eficiência Acadêmica"
					yaxislabel="Porcentagem">
					<cewolf:colorpaint color="#FFFFFF" />
					<cewolf:data>
						<cewolf:producer id="dadosIean">
							<cewolf:param name="iea" value="${ indiceAcademicoMBean.iea }" />
							<cewolf:param name="maiorIea" value="${ indiceAcademicoMBean.maiorIea }" />
							<cewolf:param name="menorIea" value="${ indiceAcademicoMBean.menorIea }" />
							<cewolf:param name="mean" value="${ indiceAcademicoMBean.meanIea }" />
							<cewolf:param name="std" value="${ indiceAcademicoMBean.stdIea }" />
						</cewolf:producer>
					</cewolf:data>
					<cewolf:chartpostprocessor id="dadosIean" />
				</cewolf:chart> <cewolf:img chartid="graficoIean" renderer="/cewolf" width="400"
					height="250" /></td>
				<td>
				<table width="100%">
					<tr>
						<td><strong>Média dos IEAs:</strong></td>
						<td><fmt:formatNumber value="${ indiceAcademicoMBean.meanIea }" pattern="##0.0000" /></td>
					</tr>
					<tr>
						<td><strong>Desvio Padrão dos IEAs:</strong></td>
						<td><fmt:formatNumber value="${ indiceAcademicoMBean.stdIea }" pattern="##0.0000" /></td>
					</tr>
					<tr>
						<td><strong>Maior IEA:</strong></td>
						<td><fmt:formatNumber value="${ indiceAcademicoMBean.maiorIea }"	pattern="##0.0000" /></td>
					</tr>
					<tr>
						<td><strong>Menor IEA:</strong></td>
						<td><fmt:formatNumber value="${ indiceAcademicoMBean.menorIea }" pattern="##0.0000" /></td>
					</tr>
				</table>
				<br/>
					<center>
					Você está entre os <strong><em><fmt:formatNumber value="${ indiceAcademicoMBean.porcentagemIea }" pattern="##0.00" />%</em></strong>
					melhores IEAs do seu curso.
					</center>
				</td>
			</tr>
		</table>
	
	</c:if> 
	
	<br/><br/>
	<h2 style="width: 100%">Descrição dos Índices Acadêmicos</h2>
	<table class="tabelaRelatorio" width="100%" id="tabelaDescricao">

		<tbody>
			<c:forEach var="indice"
				items="${ indiceAcademicoMBean.discente.discente.indices }">
				<tr>
					<td align="left"><b>${ indice.indice.sigla }</b></td>
					<td align="left">${ indice.indice.descricao }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<br>
	<table width="100%">
		<tr>
			<td align="right" class="naoImprimir"><h:outputLink
				value="#_self" id="link" tabindex="1000">
				<h:graphicImage value="/img/view.gif" styleClass="hidelink"
					id="imgFormula" />  Visualizar Fórmulas para Cálculo
        <rich:componentControl for="panel" attachTo="link"
					operation="show" event="onclick" params="" />
			</h:outputLink></td>
		</tr>
	</table>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>