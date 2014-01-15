		<td>
			<c:if test="${plano.ativo && ((planoDocenciaAssistidaMBean.portalDiscente && (plano.cadastrado || plano.solicitadoAlteracao)) || (acesso.ppg && !plano.concluido && !plano.cadastrado))}">							
				<h:commandLink title="Alterar Plano de Docência Assistida" id="btnalterarPlano_${plano.id}" action="#{planoDocenciaAssistidaMBean.preAlterar}">
					<h:graphicImage value="/img/alterar.gif"/>
					<f:setPropertyActionListener value="#{plano}" target="#{planoDocenciaAssistidaMBean.obj}"/>
				</h:commandLink>															
			</c:if>
		</td>
		<td>
			<c:if test="${(plano != null) && ((planoDocenciaAssistidaMBean.portalDiscente) || ((planoDocenciaAssistidaMBean.portalPpg) || (planoDocenciaAssistidaMBean.portalCoordenadorStricto)))}">						
				<h:commandLink title="Visualizar Plano de Docência Assistida" id="btnvisualizarPlano_${plano.id}" action="#{planoDocenciaAssistidaMBean.viewImpressao}">
					<h:graphicImage value="/img/view.gif"/>
					<f:param name="relatorioSemestral" value="false"/>
					<f:setPropertyActionListener value="#{plano}" target="#{planoDocenciaAssistidaMBean.obj}"/>
				</h:commandLink>										
			</c:if>
		</td>									
		<td>
			<c:if test="${plano.ativo && planoDocenciaAssistidaMBean.portalDiscente && (plano.aprovado || plano.solicitadoAlteracaoRelatorio)}">										
				<h:commandLink title="Preencher Relatório Semestral" id="btnpreencherRelatorio_${plano.id}" action="#{planoDocenciaAssistidaMBean.preencherRelatorioSemestral}">
					<h:graphicImage value="/img/report.png"/>
					<f:setPropertyActionListener value="#{plano}" target="#{planoDocenciaAssistidaMBean.obj}"/>
				</h:commandLink>																
			</c:if>
			<c:if test="${(plano.concluido || plano.analiseRelatorio) && plano.ativo}">
				<h:commandLink title="Visualizar Relatório Semestral" id="btnvisualizarRelatorio_${plano.id}" action="#{planoDocenciaAssistidaMBean.viewImpressao}">
					<h:graphicImage value="/img/report.png"/>
					<f:param name="relatorioSemestral" value="true"/>
					<f:setPropertyActionListener value="#{plano}" target="#{planoDocenciaAssistidaMBean.obj}"/>
				</h:commandLink>					
			</c:if>
		</td>
		<td>
			<c:if  test="${not empty plano.idArquivo}">								
				<html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${plano.idArquivo}">
					<img src="/shared/img/icones/download.png " title="Download do Arquivo Anexado" />
				</html:link>				
			</c:if>			
		</td>	
		<td>
			<c:if test="${plano != null && !plano.cadastrado && (acesso.ppg || (planoDocenciaAssistidaMBean.portalCoordenadorStricto))}">						
				<h:commandLink title="Visualizar Histórico de Movimentações" id="btnvisualizarHistorico_${plano.id}" action="#{planoDocenciaAssistidaMBean.visualizarHistorico}">
					<h:graphicImage value="/img/cal_prefs.png"/>
					<f:setPropertyActionListener value="#{plano}" target="#{planoDocenciaAssistidaMBean.obj}"/>
				</h:commandLink>										
			</c:if>
		</td>					