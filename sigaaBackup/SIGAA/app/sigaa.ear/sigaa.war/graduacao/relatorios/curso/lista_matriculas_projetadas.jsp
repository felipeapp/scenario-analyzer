<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf" %>
<f:view>
<h:form id="form">
	<h2 class="tituloTabela"><b>Lista de Matrículas Projetadas</b></h2>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano:</th>
				<td>
					${relatorioCurso.ano} a ${relatorioCurso.anoFim} 			
				</td>
			</tr>	   		
		</table>
	</div>
	<br/>
    <div class="naoImprimir" align="center">
		<div class="infoAltRem" style="width: 40%">
		    <h:graphicImage url="/img/view.gif"/> : Visualizar Relatório por Curso  <br/>
		    <h:graphicImage url="/img/listar.gif"/> : Exportar para Excel
		</div>
	</div>
	
	<br/>	
	
	<table>
		<tr>
			<td width="50%">			
				<table class="tabelaRelatorioBorda" align="center" style="width: 80%">
					<thead>
						<tr>
							<th style="text-align: center;">Ano</th>
							<th style="text-align: right;">Total</th>
							<th style="width: 30%"></th>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach items="#{relatorioCurso.listaTotais}" var="linha" varStatus="status">
							<tr class='${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}'>							
								<td style="text-align: center;">${linha.ano}</td>
								<td style="text-align: right;">
									<fmt:formatNumber pattern="#0.00" value="${linha.total}"/>
								</td>
								<td style="text-align: right;">
									<div class="naoImprimir" align="center">
										<h:commandLink title="Visualizar Relatório por Curso" action="#{relatorioCurso.geraDetalhamentoMatriculasProjetadasCurso}">
											<f:param name="ano" value="#{linha.ano}"/>
											<h:graphicImage url="/img/view.gif"/>
										</h:commandLink>
										&nbsp;														  
										<h:commandLink title="Gerar CSV" action="#{relatorioCurso.gerarDetalhamentoMatriculasProjetadasCursoCSV}">
											<f:param name="ano" value="#{linha.ano}"/>
											<h:graphicImage url="/img/listar.gif"/>
										</h:commandLink>
									</div>						
								</td>									
							</tr>
						</c:forEach>
					</tbody>
				</table>	
			</td>
			<td>
				<center> <!-- antes o tipo era verticalbar3d -->
				    <jsp:useBean id="dados" class="br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.GraficoMatriculasProjetadasAnos" scope="page" />
					<cewolf:chart id="MatriculaProjetada" type="line" xaxislabel="Ano" yaxislabel="Total Matrículas Projetadas"> 
						<cewolf:colorpaint color="#D3E1F1"/>
						<cewolf:data> 
							<cewolf:producer id="dados"> 
								<cewolf:param name="lista" value="${relatorioCurso.listaTotais}"/>
							</cewolf:producer>
						</cewolf:data> 
					</cewolf:chart> 
					<cewolf:img chartid="MatriculaProjetada" renderer="/cewolf" width="350" height="250"/> 
				</center>				
			</td>
		</tr>
	
	</table>		
</h:form>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
	