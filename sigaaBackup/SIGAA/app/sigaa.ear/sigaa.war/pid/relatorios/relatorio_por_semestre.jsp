<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<a4j:keepAlive beanName="relatorioPIDPorSemestre"/>
	<style>
		<!--
		.linkValor a {
		  text-decoration: underline;
		  color: #000000;
		  font-size: 11px;
		  font-weight: normal;
		}
		
		.totalGeral a {
			font-weight: bold;
		}
		-->
	</style>
<f:view>
	<h:form>
	    <h2 class="tituloTabela"><b>PID - Relatório por Semestre</b></h2>
	    <div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Ano-Período:</th>
					<td>
						${relatorioPIDPorSemestre.ano}.${relatorioPIDPorSemestre.periodo} 			
					</td>
				</tr>	      
			</table>
		</div>
		<br/>
		<table class="tabelaRelatorioBorda" align="center" style="width: 100%">	
			<thead>
				<tr>
					<th style="text-align: left;width: 350px;">Departamento</th>
					<th style="text-align: right;width: 50px;">Docentes Ativos</th>
					<th style="text-align: right;width: 80px;">PIDs Cadastrados</th>
					<th style="text-align: right;width: 50px;">(%)</th>
					<th style="text-align: right;width: 80px;">PIDs Homologados</th>
					<th style="text-align: right;width: 50px;">(%)</th>					
				</tr>				
			</thead>
			<c:set var="TAtivos" value="0"/>
			<c:set var="TCadastrados" value="0"/>
			<c:set var="THomologados" value="0"/>			
			<c:set var="idCentro" value="0"/>
			
			<c:set var="TAtivosCentro" value="0"/>
			<c:set var="TCadastradosCentro" value="0"/>
			<c:set var="THomologadosCentro" value="0"/>			
			
			<c:set var="siglaCentro" value=""/>
			<tbody>
				<c:forEach items="#{relatorioPIDPorSemestre.listagem}" varStatus="loop" var="linha" >   
				
					<c:set var="TAtivos" value="${TAtivos + linha.totalDocentesAtivos}"/>
					<c:set var="TCadastrados" value="${TCadastrados + linha.totalCadastrados}"/>
					<c:set var="THomologados" value="${THomologados + linha.totalHomologados}"/>
							 		
					<c:if test="${idCentro != linha.unidade.gestora.id}">
						<c:if test="${!loop.first}">
							<tr class="linhaCinza">
								<th style="text-align: right;">Total (${siglaCentro})</th>
								<th class="linkValor" style="text-align: right;">
									<c:if test="${TAtivosCentro > 0 }">
										<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
											<ufrn:format type="valorint" valor="${ TAtivosCentro }"/>
											<f:param name="id" value="0"/>
											<f:param name="tipo" value="0"/>
											<f:param name="idCentro" value="#{idCentro}"/>
										</h:commandLink>
									</c:if>
									<c:if test="${TAtivosCentro == 0 }">
										<h:outputText value="0"/>
									</c:if>					
								</th>
								<th style="text-align: right;" class="linkValor">
									<c:if test="${TCadastradosCentro > 0 }">
										<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
											<ufrn:format type="valorint" valor="${ TCadastradosCentro }"/>
											<f:param name="id" value="0"/>
											<f:param name="tipo" value="1"/>
											<f:param name="idCentro" value="#{idCentro}"/>
										</h:commandLink>
									</c:if>
									<c:if test="${TCadastradosCentro == 0 }">
										<h:outputText value="0"/>
									</c:if>					
								</th>
								<th style="text-align: right;">
									<fmt:formatNumber pattern="#0.00" value="${(TCadastradosCentro / TAtivosCentro)*100}"/>%
								</th>
								<th style="text-align: right;" class="linkValor">
									<c:if test="${THomologadosCentro > 0 }">
										<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
											<ufrn:format type="valorint" valor="${ THomologadosCentro }"/>
											<f:param name="id" value="0"/>
											<f:param name="tipo" value="2"/>
											<f:param name="idCentro" value="#{idCentro}"/>
										</h:commandLink>
									</c:if>
									<c:if test="${THomologadosCentro == 0 }">
										<h:outputText value="0"/>
									</c:if>						
								</th>
								<th style="text-align: right;">
									<fmt:formatNumber pattern="#0.00" value="${(THomologadosCentro / TAtivosCentro)*100}"/>%
								</th>								
							</tr>		
							<tr>
								<td colspan="6">&nbsp;</td>
							</tr>				
						</c:if>
						
						<c:set var="TAtivosCentro" value="0"/>
						<c:set var="TCadastradosCentro" value="0"/>
						<c:set var="THomologadosCentro" value="0"/>
												
						<tr class="linhaCinza">
							<td colspan="6" style="text-align: center;">${linha.unidade.gestora.sigla}</td>
							<c:set var="siglaCentro" value="${linha.unidade.gestora.sigla}"/>
						</tr>					
					</c:if>
					
					<c:set var="TAtivosCentro" value="${TAtivosCentro + linha.totalDocentesAtivos}"/>
					<c:set var="TCadastradosCentro" value="${TCadastradosCentro + linha.totalCadastrados}"/>
					<c:set var="THomologadosCentro" value="${THomologadosCentro + linha.totalHomologados}"/>					
					
					<c:set var="idCentro" value="#{linha.unidade.gestora.id}"/>
							 		
					<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: left;">
							${linha.unidade.nome}
						</td>						
						<td style="text-align: right;" class="linkValor">
							<c:if test="${linha.totalDocentesAtivos > 0 }">
								<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
									<ufrn:format type="valorint" valor="${ linha.totalDocentesAtivos}"/>
									<f:param name="id" value="#{linha.unidade.id}"/>
									<f:param name="tipo" value="0"/>
								</h:commandLink>
							</c:if>
							<c:if test="${linha.totalDocentesAtivos == 0 }">
								<h:outputText value="0"/>
							</c:if>
						</td>
						<td style="text-align: right;" class="linkValor">
							<c:if test="${linha.totalCadastrados > 0 }">
								<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
									<ufrn:format type="valorint" valor="${ linha.totalCadastrados}"/>
									<f:param name="id" value="#{linha.unidade.id}"/>
									<f:param name="tipo" value="1"/>
								</h:commandLink>
							</c:if>
							<c:if test="${linha.totalCadastrados == 0 }">
								<h:outputText value="0"/>
							</c:if>																	
						</td>		
						<td style="text-align: right;">
							<fmt:formatNumber pattern="#0.00" value="${(linha.totalCadastrados / linha.totalDocentesAtivos)*100}"/>%
						</td>											
						<td style="text-align: right;" class="linkValor">
							<c:if test="${linha.totalHomologados > 0 }">
								<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
									<ufrn:format type="valorint" valor="${ linha.totalHomologados}"/>
									<f:param name="id" value="#{linha.unidade.id}"/>
									<f:param name="tipo" value="2"/>
								</h:commandLink>
							</c:if>
							<c:if test="${linha.totalHomologados == 0 }">
								<h:outputText value="0"/>
							</c:if>												
						</td>	
						<td style="text-align: right;">
							<fmt:formatNumber pattern="#0.00" value="${(linha.totalHomologados / linha.totalDocentesAtivos)*100}"/>%
						</td>												
					</tr>
				</c:forEach>	  
			</tbody>	
			<tr class="linhaCinza">
				<th style="text-align: right;">Total (${siglaCentro})</th>
				<th class="linkValor" style="text-align: right;">
					<c:if test="${TAtivosCentro > 0 }">
						<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
							<ufrn:format type="valorint" valor="${ TAtivosCentro }"/>
							<f:param name="id" value="0"/>
							<f:param name="tipo" value="0"/>
							<f:param name="idCentro" value="#{idCentro}"/>
						</h:commandLink>
					</c:if>
					<c:if test="${TAtivosCentro == 0 }">
						<h:outputText value="0"/>
					</c:if>					
				</th>
				<th style="text-align: right;" class="linkValor">
					<c:if test="${TCadastradosCentro > 0 }">
						<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
							<ufrn:format type="valorint" valor="${ TCadastradosCentro }"/>
							<f:param name="id" value="0"/>
							<f:param name="tipo" value="1"/>
							<f:param name="idCentro" value="#{idCentro}"/>
						</h:commandLink>
					</c:if>
					<c:if test="${TCadastradosCentro == 0 }">
						<h:outputText value="0"/>
					</c:if>					
				</th>
				<td style="text-align: right;">
					<fmt:formatNumber pattern="#0.00" value="${(TCadastradosCentro / TAtivosCentro)*100}"/>%
				</td>					
				<th style="text-align: right;" class="linkValor">
					<c:if test="${THomologadosCentro > 0 }">
						<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
							<ufrn:format type="valorint" valor="${ THomologadosCentro }"/>
							<f:param name="id" value="0"/>
							<f:param name="tipo" value="2"/>
							<f:param name="idCentro" value="#{idCentro}"/>
						</h:commandLink>
					</c:if>
					<c:if test="${THomologadosCentro == 0 }">
						<h:outputText value="0"/>
					</c:if>						
				</th>
				<td style="text-align: right;">
					<fmt:formatNumber pattern="#0.00" value="${(THomologadosCentro / TAtivosCentro)*100}"/>%
				</td>						
			</tr>			
			<tr>
				<td colspan="6">&nbsp;</td>
			</tr>				
			<tr class="linhaCinza">
				<th style="text-align: right;">TOTAIS</th>
				<th class="linkValor" style="text-align: right;">
					<c:if test="${TAtivos > 0 }">
						<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
							<ufrn:format type="valorint" valor="${ TAtivos }"/>
							<f:param name="id" value="0"/>
							<f:param name="tipo" value="0"/>
						</h:commandLink>
					</c:if>
					<c:if test="${TAtivos == 0 }">
						<h:outputText value="0"/>
					</c:if>					
				</th>
				<th style="text-align: right;" class="linkValor">
					<c:if test="${TCadastrados > 0 }">
						<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
							<ufrn:format type="valorint" valor="${ TCadastrados }"/>
							<f:param name="id" value="0"/>
							<f:param name="tipo" value="1"/>
						</h:commandLink>
					</c:if>
					<c:if test="${TCadastrados == 0 }">
						<h:outputText value="0"/>
					</c:if>					
				</th>
				<th style="text-align: right;">
					<fmt:formatNumber pattern="#0.00" value="${(TCadastrados / TAtivos)*100}"/>%
				</th>					
				<th style="text-align: right;" class="linkValor">
					<c:if test="${THomologados > 0 }">
						<h:commandLink action="#{relatorioPIDPorSemestre.detalharRelatorio}">
							<ufrn:format type="valorint" valor="${ THomologados }"/>
							<f:param name="id" value="0"/>
							<f:param name="tipo" value="2"/>
						</h:commandLink>
					</c:if>
					<c:if test="${THomologados == 0 }">
						<h:outputText value="0"/>
					</c:if>						
				</th>
				<th style="text-align: right;">
					<fmt:formatNumber pattern="#0.00" value="${(THomologados / TAtivos)*100}"/>%
				</th>						
			</tr>
		</table>	
	</h:form>			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>