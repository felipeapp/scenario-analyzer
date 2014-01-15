<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> Notificações quanto ao envio de relatórios de extensão </h2>

<jwr:style src="/css/ensino/notificacoes.css" media="all" />

<style>
div.logon h3{ font-size: 12px !important;padding-right:15px;}
.confirmaSenha { float: left !important; }
</style>

<f:view>
	<h:form>

	<img src="${ctx}/img/notificacao/imagemNotificacoes.png" style="padding-left:20px;float:left"/>
	
	<div class="intro">
		<div class="textos">
			<p style="text-align: justify;">
				Prezado Coordenador de Ação de Extensão Universitária, 
				Considerando a necessidade de assegurar a consolidação de informações 
				para os relatórios institucionais, bem como em obediência à determinação 
				da <b>Resolução 053/2008 - CONSEPE, de 15 de Abril de 2008</b>, que dispõe sobre 
				as Normas que Regulamentam as Ações de Extensão Universitária na UFRN, 
				<b>solicitamos a Vossa Senhoria o preenchimento dos relatórios parciais/finais das 
				ações de extensão universitária e o seu devido envio via SIGAA</b>. 
				Oportuno esclarecer que de acordo com o Artigo 31 da citada Resolução, os Coordenadores 
				de quaisquer ações de Extensão Universitária devem apresentar o Relatório Final à PROEX, 
				até no máximo 30 (trinta) dias após a data prevista de conclusão da atividade e que a não 
				apresentação do relatório final ou parcial ao final do exercício implicará em não aprovação
				de Projetos futuros. Na ocasião, ressaltamos que para UFRN cumprir com a sua missão é importante
				a prestação de informações coerentes com a realidade aos órgãos de controle externo, e por isso 
				solicitamos mais uma vez a vossa senhoria que preencha os relatórios final ou parcial no SIGAA das 
				ações de extensão sob vossa responsabilidade e registre explicitamente os impactos causados na 
				comunidade bem como a sua relevância social. Outro aspecto imprescindível a ser registrado é a relação
				existente da atividade de extensão com o ensino e/ou a pesquisa, enfatizando a relevância acadêmica da
				ação e a sua influência para uma formação cidadã. <br/><br/>
	
				Na certeza do atendimento à solicitação, agradecemos antecipadamente o empenho de Vossa Senhoria no desenvolvimento de atividades acadêmicas no campo da Extensão Universitária.<br/><br/> 
	
				Atenciosamente, <br/><br/>
	
				Assessoria Técnica da PROEx.<br/><br/>
	
				3215-3234
			<p>
		</div>		
			
			
			
			<br clear="all"/>
	</div>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/document_add.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relatório Parcial	    
	    <h:graphicImage value="/img/extensao/document_new.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relatório Final<br/>
	    <h:graphicImage value="/img/extensao/document_edit.png"style="overflow: visible;" styleClass="noborder"/>: Editar/Enviar Relatório
	    <h:graphicImage value="/img/extensao/document_delete.png"style="overflow: visible;" styleClass="noborder"/>: Remover Relatório
	    <h:graphicImage value="/img/extensao/document_view.png"style="overflow: visible;" styleClass="noborder"/>: Ver Relatório	    
	</div>
	
	
	<div>
			<table class="listagem">
					<caption class="listagem"> Ações coordenadas pelo usuário atual e com pendências quanto ao envio de relatórios  </caption>
			<thead>
			<tr>
					<th width="15%">Tipo Relatório</th>
					<th style="text-align: center;">Data de Envio</th>
					<th>Validado Depto.</th>
					<th width="20%">Justificativa Depto.</th>					
					<th width="20%">Validado PROEx</th>
					<th width="20%">Justificativa Proex</th>
					<th></th>
					<th></th>
					<th></th>					
			</tr>
			</thead>
			<tbody>
			
					<c:set value="#{relatorioAcaoExtensao.atividadesPendentesRelatoriosCoordenador}" var="acoes"/>			
					<c:forEach items="#{acoes}" var="acao" varStatus="status">						
						
						<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
							<td colspan="7" >
								
								<table>
									<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<th><b>Código:</b></th>
										<td>${ acao.codigo }</td>
									</tr>
									<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<th><b>Título:</b></th>
										<td>${ acao.titulo }</td>
									</tr>
									<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<th><b>Início:</b></th>
										<td> <fmt:formatDate value="${acao.projeto.dataInicio}" pattern="dd/MM/yyyy"/> </td>
									</tr>
									<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<th><b>Fim:</b></th>
										<td> <fmt:formatDate value="${acao.projeto.dataFim}" pattern="dd/MM/yyyy"/> </td>
									</tr>
									<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<th><b>Situação:</b></th>
										<td>
											<c:if test="${!acao.prazoExpiradoParaConclusao}">
												Finalizará em <b>${acao.totalDiasConclusao}</b> dia(s) 
											</c:if>
								
											<c:if test="${acao.prazoExpiradoParaConclusao}">
												Finalizou há <font color='red'><b>${acao.totalDiasConclusao * -1}</b></font> dia(s)
											</c:if>
										</td>
									</tr>
								</table>							
							</td>
							<td width="2%">
							
								<h:commandLink action="#{relatorioAcaoExtensao.preAdicionarRelatorio}" style="border: 0;"	id="relParcial">
							       <f:param name="id" value="#{acao.id}"/>
							       <f:param name="relatorioFinal" value="false"/>
							       <f:param name="telaNotificacoes" value="true"/>
					               <h:graphicImage url="/img/extensao/document_add.png" title="Cadastrar Relatório Parcial" />
								</h:commandLink>							
								
							</td>	
							<td width="2%">
								<h:commandLink action="#{relatorioAcaoExtensao.preAdicionarRelatorio}" style="border: 0;"	id="relFinal">
							       <f:param name="id" value="#{acao.id}"/>
							       <f:param name="relatorioFinal" value="true"/>
							       <f:param name="telaNotificacoes" value="true"/>
					               <h:graphicImage url="/img/extensao/document_new.png" title="Cadastrar Relatório Final" />
								</h:commandLink>
							</td>																		
							
						</tr>
						
						
						<c:forEach items="#{acao.relatorios}" var="item" varStatus="status">
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			               			<td width="15%">${item.tipoRelatorio.descricao}</td>
									<td style="text-align: center;">
										<fmt:formatDate value="${item.dataEnvio}" pattern="dd/MM/yyyy" />
										<h:outputText value="CADASTRO EM ANDAMENTO" rendered="#{empty item.dataEnvio}"/>
									</td>
									<td>
										<font color="${(item.reprovadoDepartamento || item.aprovadoComRecomendacoesDepartamento) ? 'red' : 'black'}">
											${item.tipoParecerDepartamento.descricao}
										</font>
										
										<c:if test="${(item.reprovadoDepartamento || item.aprovadoComRecomendacoesDepartamento)}">
											<ufrn:help>Relatórios 'REPROVADOS' ou 'APROVADOS COM RECOMENDAÇÃO' devem ser alterados e enviados para nova validação.</ufrn:help>
										</c:if>
										
									</td>
									<td width="20%">${item.parecerDepartamento}</td>
									<td>
										<font color="${(item.reprovadoProex || item.aprovadoComRecomendacoesProex) ? 'red' : 'black'}">
											${item.tipoParecerProex.descricao}
										</font>										
										<c:if test="${(item.reprovadoProex || item.aprovadoComRecomendacoesProex)}">
											<ufrn:help>Relatórios 'REPROVADOS' ou 'APROVADOS COM RECOMENDAÇÃO' devem ser alterados e enviados para nova validação.</ufrn:help>
										</c:if>									
									</td>
									<td width="20%">${item.parecerProex}</td>
									<td width="2%">
										<h:commandLink action="#{relatorioAcaoExtensao.preAlterarRelatorio}" style="border: 0;" 
										id="alterarRelatorio" rendered="#{item.editavel}">
									       <f:param name="idRelatorio" value="#{item.id}"/>
									       <f:param name="telaNotificacoes" value="true"/>
							               <h:graphicImage url="/img/extensao/document_edit.png" title="Editar/Enviar Relatório" />
										</h:commandLink>
									</td>																		
									
									<td width="2%">
										<h:commandLink action="#{relatorioAcaoExtensao.removerRelatorio}" style="border: 0;" 
										id="removerRelatorio" rendered="#{item.editavel}" onclick="#{confirmDelete}">
									       <f:param name="idRelatorio" value="#{item.id}"/>
									       <f:param name="telaNotificacoes" value="true"/>
							               <h:graphicImage url="/img/extensao/document_delete.png" title="Remover Relatório"/>
										</h:commandLink>
									</td>
									
									<td width="2%">								               
										<h:commandLink action="#{relatorioAcaoExtensao.view}" style="border: 0;" id="verRelatorio">
										   <f:param name="id" value="#{item.id}"/>
										   <f:param name="telaNotificacoes" value="true"/>
								           <h:graphicImage url="/img/extensao/document_view.png" title="Ver Relatório"/>
										</h:commandLink>
									</td>								
									
							</tr>
						</c:forEach>
						
						<c:if test="${empty acao.relatorios}" >
			 		   		<tr><td colspan="8" align="center"><font color="red">Não há relatórios cadastrados para esta ação.</font></td></tr>
			 		    </c:if>
						
					</c:forEach>
			 		   
			 		<c:if test="${empty acoes}" >
			 		 		<tr><td colspan="6" align="center"><font color="red">Não há ações de extensão ativas coordenadas pelo usuário atual.</font></td></tr>
			 		</c:if>
			 		   
			</tbody>
	</table>
	
	</div>
	
	
		
	</h:form>
	
	<br/>
	<br/>
	<br/>
</f:view>	

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>