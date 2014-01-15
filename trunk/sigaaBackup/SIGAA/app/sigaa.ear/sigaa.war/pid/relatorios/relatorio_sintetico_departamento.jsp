<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>
	<h:form>
	    <h2 class="tituloTabela"><b>Relatório Sintético por Departamento (PID)</b></h2>
	    <div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Departamento:</th>
					<td>
						${relatorioPID.usuarioLogado.servidor.unidade.nome} 			
					</td>
				</tr>	      
			</table>
		</div>
		<br/>
		<p style="text-align: left;">
			<b>Legendas:</b><br/>
			<div class="naoImprimir">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Plano de Individual do Docente<br/>
			</div>
			<b>Admin.</b>: Carga Horária de Administração, <b>Grupo V</b>: Carga Horária Grupo V - CONSEPE
		</p>
		<table class="tabelaRelatorioBorda" align="center" style="width: 100%">	
			<thead>
				<tr>
					<th style="text-align: left;width: 350px;" rowspan="4">Docente</th>
					<th style="text-align: left;width: 100px;" rowspan="4">Classe Funcional</th>
				</tr>
				<tr>
					<th colspan="7" style="text-align: center;">Carga Horária</th>
					<th class="naoImprimir" rowspan="3" style="text-align: center;width: 5px;"></th>				
				</tr>
				<tr>
					<th style="text-align: center;" rowspan="2">
						Ensino					
					</th>
					<th style="text-align: center;" colspan="6">
						Outras Atividades
					</th>
				</tr>				
				<tr>
					<th style="text-align: right;width: 50px;">Outras Ensino</th>
					<th style="text-align: right;width: 50px;">Pesquisa</th>
					<th style="text-align: right;width: 50px;">Extensão</th>
					<th style="text-align: right;width: 50px;">Admin.</th>
					<th style="text-align: right;width: 50px;">Grupo V</th>
					<th style="text-align: right;width: 50px;">Total</th>
				</tr>
			</thead>
				
			<tbody>
				<c:forEach items="#{relatorioPID.listagem}" varStatus="loop" var="linha" >    		
					<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: left;">
							${linha.servidor.pessoa.nome}
						</td>
						<td style="text-align: left;">
							${linha.servidor.classeFuncional.denominacao}
							<c:if test="${linha.servidor.dedicacaoExclusiva}">
								<h:outputText value="(DE)"/>
							</c:if>
							<c:if test="${not linha.servidor.dedicacaoExclusiva}">
								(${linha.servidor.regimeTrabalho}h)
							</c:if>
						</td>
						<td style="text-align: right; font-weight: bold;">
							<ufrn:format type="valorint" valor="${ linha.totalGrupoEnsino}"/>
						</td>
						<td style="text-align: right;">
							<ufrn:format type="valorint" valor="${linha.chOutrasAtividades.chSemanalOutrasAtividadesEnsino}"/>
						</td>						
						<td style="text-align: right;">
							<ufrn:format type="valorint" valor="${linha.chProjeto.chPesquisa}"/>
						</td>
						<td style="text-align: right;">
							<ufrn:format type="valorint" valor="${linha.chProjeto.chExtensao}"/>
						</td>
						<td style="text-align: right;">
							<ufrn:format type="valorint" valor="${linha.chTotalAdministracao}"/>
						</td>
						<td style="text-align: right;">
							<ufrn:format type="valorint" valor="${linha.chOutrasAtividades.chSemanalOutrasAtividades}"/>
						</td>
						<td style="text-align: right; font-weight: bold;">
							<ufrn:format type="valorint" valor="${linha.totalGrupoOutrasAtividades}"/>
						</td>					
						<td style="text-align: right;" class="naoImprimir">
								<h:commandLink title="Visualizar Plano Individual do Docente" target="_blank" action="#{cargaHorariaPIDMBean.visualizarPID}">
									<h:graphicImage value="/img/view.gif"/>
									<f:param name="id" value="#{linha.id}"/>
								</h:commandLink>
						</td>
					</tr>
				</c:forEach>	  
			</tbody>		
		</table>	
	</h:form>			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>