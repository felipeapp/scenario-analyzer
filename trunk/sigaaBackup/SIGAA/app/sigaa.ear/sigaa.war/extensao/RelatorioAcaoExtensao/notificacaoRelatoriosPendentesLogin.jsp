<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> Notifica��es quanto ao envio de relat�rios de extens�o </h2>

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
				Prezado Coordenador de A��o de Extens�o Universit�ria, 
				Considerando a necessidade de assegurar a consolida��o de informa��es 
				para os relat�rios institucionais, bem como em obedi�ncia � determina��o 
				da <b>Resolu��o 053/2008 - CONSEPE, de 15 de Abril de 2008</b>, que disp�e sobre 
				as Normas que Regulamentam as A��es de Extens�o Universit�ria na UFRN, 
				<b>solicitamos a Vossa Senhoria o preenchimento dos relat�rios parciais/finais das 
				a��es de extens�o universit�ria e o seu devido envio via SIGAA</b>. 
				Oportuno esclarecer que de acordo com o Artigo 31 da citada Resolu��o, os Coordenadores 
				de quaisquer a��es de Extens�o Universit�ria devem apresentar o Relat�rio Final � PROEX, 
				at� no m�ximo 30 (trinta) dias ap�s a data prevista de conclus�o da atividade e que a n�o 
				apresenta��o do relat�rio final ou parcial ao final do exerc�cio implicar� em n�o aprova��o
				de Projetos futuros. Na ocasi�o, ressaltamos que para UFRN cumprir com a sua miss�o � importante
				a presta��o de informa��es coerentes com a realidade aos �rg�os de controle externo, e por isso 
				solicitamos mais uma vez a vossa senhoria que preencha os relat�rios final ou parcial no SIGAA das 
				a��es de extens�o sob vossa responsabilidade e registre explicitamente os impactos causados na 
				comunidade bem como a sua relev�ncia social. Outro aspecto imprescind�vel a ser registrado � a rela��o
				existente da atividade de extens�o com o ensino e/ou a pesquisa, enfatizando a relev�ncia acad�mica da
				a��o e a sua influ�ncia para uma forma��o cidad�. <br/><br/>
	
				Na certeza do atendimento � solicita��o, agradecemos antecipadamente o empenho de Vossa Senhoria no desenvolvimento de atividades acad�micas no campo da Extens�o Universit�ria.<br/><br/> 
	
				Atenciosamente, <br/><br/>
	
				Assessoria T�cnica da PROEx.<br/><br/>
	
				3215-3234
			<p>
		</div>		
			
			
			
			<br clear="all"/>
	</div>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/document_add.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Parcial	    
	    <h:graphicImage value="/img/extensao/document_new.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Final<br/>
	    <h:graphicImage value="/img/extensao/document_edit.png"style="overflow: visible;" styleClass="noborder"/>: Editar/Enviar Relat�rio
	    <h:graphicImage value="/img/extensao/document_delete.png"style="overflow: visible;" styleClass="noborder"/>: Remover Relat�rio
	    <h:graphicImage value="/img/extensao/document_view.png"style="overflow: visible;" styleClass="noborder"/>: Ver Relat�rio	    
	</div>
	
	
	<div>
			<table class="listagem">
					<caption class="listagem"> A��es coordenadas pelo usu�rio atual e com pend�ncias quanto ao envio de relat�rios  </caption>
			<thead>
			<tr>
					<th width="15%">Tipo Relat�rio</th>
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
										<th><b>C�digo:</b></th>
										<td>${ acao.codigo }</td>
									</tr>
									<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<th><b>T�tulo:</b></th>
										<td>${ acao.titulo }</td>
									</tr>
									<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<th><b>In�cio:</b></th>
										<td> <fmt:formatDate value="${acao.projeto.dataInicio}" pattern="dd/MM/yyyy"/> </td>
									</tr>
									<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<th><b>Fim:</b></th>
										<td> <fmt:formatDate value="${acao.projeto.dataFim}" pattern="dd/MM/yyyy"/> </td>
									</tr>
									<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<th><b>Situa��o:</b></th>
										<td>
											<c:if test="${!acao.prazoExpiradoParaConclusao}">
												Finalizar� em <b>${acao.totalDiasConclusao}</b> dia(s) 
											</c:if>
								
											<c:if test="${acao.prazoExpiradoParaConclusao}">
												Finalizou h� <font color='red'><b>${acao.totalDiasConclusao * -1}</b></font> dia(s)
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
					               <h:graphicImage url="/img/extensao/document_add.png" title="Cadastrar Relat�rio Parcial" />
								</h:commandLink>							
								
							</td>	
							<td width="2%">
								<h:commandLink action="#{relatorioAcaoExtensao.preAdicionarRelatorio}" style="border: 0;"	id="relFinal">
							       <f:param name="id" value="#{acao.id}"/>
							       <f:param name="relatorioFinal" value="true"/>
							       <f:param name="telaNotificacoes" value="true"/>
					               <h:graphicImage url="/img/extensao/document_new.png" title="Cadastrar Relat�rio Final" />
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
											<ufrn:help>Relat�rios 'REPROVADOS' ou 'APROVADOS COM RECOMENDA��O' devem ser alterados e enviados para nova valida��o.</ufrn:help>
										</c:if>
										
									</td>
									<td width="20%">${item.parecerDepartamento}</td>
									<td>
										<font color="${(item.reprovadoProex || item.aprovadoComRecomendacoesProex) ? 'red' : 'black'}">
											${item.tipoParecerProex.descricao}
										</font>										
										<c:if test="${(item.reprovadoProex || item.aprovadoComRecomendacoesProex)}">
											<ufrn:help>Relat�rios 'REPROVADOS' ou 'APROVADOS COM RECOMENDA��O' devem ser alterados e enviados para nova valida��o.</ufrn:help>
										</c:if>									
									</td>
									<td width="20%">${item.parecerProex}</td>
									<td width="2%">
										<h:commandLink action="#{relatorioAcaoExtensao.preAlterarRelatorio}" style="border: 0;" 
										id="alterarRelatorio" rendered="#{item.editavel}">
									       <f:param name="idRelatorio" value="#{item.id}"/>
									       <f:param name="telaNotificacoes" value="true"/>
							               <h:graphicImage url="/img/extensao/document_edit.png" title="Editar/Enviar Relat�rio" />
										</h:commandLink>
									</td>																		
									
									<td width="2%">
										<h:commandLink action="#{relatorioAcaoExtensao.removerRelatorio}" style="border: 0;" 
										id="removerRelatorio" rendered="#{item.editavel}" onclick="#{confirmDelete}">
									       <f:param name="idRelatorio" value="#{item.id}"/>
									       <f:param name="telaNotificacoes" value="true"/>
							               <h:graphicImage url="/img/extensao/document_delete.png" title="Remover Relat�rio"/>
										</h:commandLink>
									</td>
									
									<td width="2%">								               
										<h:commandLink action="#{relatorioAcaoExtensao.view}" style="border: 0;" id="verRelatorio">
										   <f:param name="id" value="#{item.id}"/>
										   <f:param name="telaNotificacoes" value="true"/>
								           <h:graphicImage url="/img/extensao/document_view.png" title="Ver Relat�rio"/>
										</h:commandLink>
									</td>								
									
							</tr>
						</c:forEach>
						
						<c:if test="${empty acao.relatorios}" >
			 		   		<tr><td colspan="8" align="center"><font color="red">N�o h� relat�rios cadastrados para esta a��o.</font></td></tr>
			 		    </c:if>
						
					</c:forEach>
			 		   
			 		<c:if test="${empty acoes}" >
			 		 		<tr><td colspan="6" align="center"><font color="red">N�o h� a��es de extens�o ativas coordenadas pelo usu�rio atual.</font></td></tr>
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