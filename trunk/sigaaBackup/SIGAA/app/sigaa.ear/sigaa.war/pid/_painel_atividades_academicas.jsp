<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<script type="text/javascript">

var J = jQuery.noConflict();

function habilitarDetalhes(idAtividade) {
	var linha = 'linha_'+ idAtividade;

	if ( J('#'+linha).css('display') == 'none' )
		if (/msie/.test( navigator.userAgent.toLowerCase() ))
			J('#'+linha).css('display', 'inline-block');
		else
			J('#'+linha).css('display', 'table-cell');

		//exibe a seta que exibe a caixa de observação
		J('#'+'linha_cima_'+idAtividade).css('display', 'inline-block');
		J('#'+'linha_baixo_'+idAtividade).css('display', 'none');
}

function esconderDetalhes(idAtividade) {
	var linha = 'linha_'+ idAtividade;

	if ( J('#'+linha).css('display') != 'none' )
		if (/msie/.test( navigator.userAgent.toLowerCase() ))
			J('#'+linha).css('display', 'none');
		else
			J('#'+linha).css('display', 'none');
	
	//esconde a seta que exibe a caixa de observação
	J('#'+'linha_cima_'+idAtividade).css('display', 'none');
	J('#'+'linha_baixo_'+idAtividade).css('display', 'inline-block');
}

</script>
<rich:panel header="II - OUTRAS ATIVIDADES DE ENSINO, PESQUISA, EXTENSÃO E ADMINISTRAÇÃO" styleClass="painelAtividades">
	
	<div class="quadroTotais">
		<h:outputText value="#{cargaHorariaPIDMBean.obj.servidor.regimeTrabalho}" /> (CH do regime de trabalho)	
		-		
		<a4j:outputPanel id="outputTotalEnsinoUsado1">
			<h:outputText value="#{cargaHorariaPIDMBean.obj.totalGrupoEnsino}"> 
				<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
			</h:outputText> (CH dedicada ao ensino)
		</a4j:outputPanel>
			=
		<a4j:outputPanel id="outputTotalEnsinoRestante"> 
			<h:outputText value="#{cargaHorariaPIDMBean.chRestanteADistribuir}">
				<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
			</h:outputText>
		</a4j:outputPanel>h
	</div>
	
	<p> 
		Distribua percentualmente as
		<a4j:outputPanel id="outputTotalEnsinoUsado2"> 
			<h:outputText value="#{cargaHorariaPIDMBean.chRestanteADistribuir}"><f:convertNumber maxFractionDigits="1" groupingUsed="false" /></h:outputText>horas restantes nas demais atividades abaixo.
		</a4j:outputPanel>
	</p>
		
		<br><br> 
				
				<table class="listagem" style="border-bottom: 1px solid #C0C0C0; border-left: 1px solid #C0C0C0; border-right: 1px solid #C0C0C0; border-top: 1px solid #C0C0C0;">
					<tr>
						<td style="background-color: #ECF4FE;">
							<b>Atividade</b>
						</td>
						
						<td style="background-color: #ECF4FE;">
							<b>Dados</b> 
						</td>
						
						<td style="background-color: #ECF4FE; text-align: right;">
							<b>Percentual de Dedicação</b> 
						</td>
						
						<td style="background-color: #ECF4FE; text-align: right;">
							<b>CH Semanal Dedicada</b> 
						</td>
					</tr>
					
					<tr>
						<td>OUTRAS ATIVIDADES DE ENSINO</td>
						<td>${fn:length(cargaHorariaPIDMBean.listaCargaHorariaProjetoEnsino)} 
						
						    	<h:outputLink value="#_self" id="linkEnsinoProducaoAcademica" tabindex="20000">
									projetos de ensino ativo(s).
							        <rich:componentControl for="panellinkEnsinoProducaoAcademica" attachTo="linkEnsinoProducaoAcademica" operation="show" event="onclick"/>
							    </h:outputLink>
					    </td>
						
						<td align="right">					
							<a4j:region> 
								<h:inputText value="#{cargaHorariaPIDMBean.obj.chOutrasAtividades.percentualOutrasAtividadesEnsino}" maxlength="3" size="3" id="chAtendAtivEnsino" title="Outras Atividades de Ensino" onkeyup="return formatarInteiro(this);">
									<a4j:support
										reRender="outputAtivEnsino, outputChOutrasAtiv, outputResumo, listagemCheckBoxEnsino, legenda" 
										actionListener="#{cargaHorariaPIDMBean.calcularHorarioPorPercentualOutrasAtividadesEnsino}"
										event="onblur"></a4j:support>
								</h:inputText>%
							</a4j:region>
						</td>
						
						<td align="right">
							<a4j:outputPanel id="outputAtivEnsino"> 
								<h:outputText value="#{cargaHorariaPIDMBean.obj.chOutrasAtividades.chSemanalOutrasAtividadesEnsino}" id="chAtendOutrasAtivEnsinoHorario">
									<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
								</h:outputText>h
							</a4j:outputPanel>
						</td>
					</tr>
					
					<tr>
						<td>PESQUISA E PRODUÇÃO ACADÊMICA</td>
						<td>${fn:length(cargaHorariaPIDMBean.listaCargaHorariaProjetoPesquisa)} 
						
						    	<h:outputLink value="#_self" id="linkPesquisaProducaoAcademica" tabindex="20000">
									projetos ativo(s).
							        <rich:componentControl for="panellinkPesquisaProducaoAcademica" attachTo="linkPesquisaProducaoAcademica" operation="show" event="onclick"/>
							    </h:outputLink>
						</td>
						
						<td align="right">
								<a4j:region> 
									<h:inputText value="#{cargaHorariaPIDMBean.obj.chProjeto.percentualPesquisa}" maxlength="3" size="3" id="chAtendPesquisaPercentual" title="Pesquisa Produção Acadêmica" onkeyup="return formatarInteiro(this);">
										<a4j:support
										reRender="outputChHoraPesquisa, outputChOutrasAtiv, outputResumo, listagemCheckBoxPesquisa, legenda" 
										actionListener="#{cargaHorariaPIDMBean.calcularHorarioPorPercentualPesquisa}" event="onblur"></a4j:support>
									</h:inputText>%
								</a4j:region>
						</td>
						
						<td align="right">
							<a4j:outputPanel id="outputChHoraPesquisa">
								<h:outputText value="#{cargaHorariaPIDMBean.obj.chProjeto.chPesquisa}" id="chAtendPesquisaHorario">
								   <f:convertNumber maxFractionDigits="1" groupingUsed="false" />
							   </h:outputText>h
							</a4j:outputPanel>
						</td>
					</tr>
					
					<tr>
						<td>EXTENSÃO E OUTRAS ATIVIDADES</td>
						<td>${fn:length(cargaHorariaPIDMBean.listaCargaHorariaProjetoExtensao)}
						
						    	<h:outputLink value="#_self" id="linkExtensao" tabindex="20000">
									ações ativa(s).
							        <rich:componentControl for="panelExtensao" attachTo="linkExtensao" operation="show" event="onclick"/>
							    </h:outputLink>
						</td>
						
						<td align="right">
								<a4j:region> 
									<h:inputText value="#{cargaHorariaPIDMBean.obj.chProjeto.percentualExtensao}" maxlength="3" size="3" id="chAtendExtensao" title="Extensão e Outras Atividades" onkeyup="return formatarInteiro(this);">
										<a4j:support
										reRender="outputChHoraExtensao, outputChOutrasAtiv, outputResumo, listagemCheckBoxExtensaoOutrasAtividTecnicas, legenda" 
										actionListener="#{cargaHorariaPIDMBean.calcularHorarioPorPercentualExtensao}" event="onblur"></a4j:support>
									</h:inputText>%
								</a4j:region>
						</td>
						
						<td align="right">
							<a4j:outputPanel id="outputChHoraExtensao"> 
								<h:outputText value="#{cargaHorariaPIDMBean.obj.chProjeto.chExtensao}" id="chAtendExtensaoHorario">
									<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
								</h:outputText>h
							</a4j:outputPanel>
						</td>
					</tr>
					
					<tr>
						<td>FUNÇÕES ADMINISTRATIVAS</td>
						<td> 
							<c:set var="tmp" value="${ fn:length(cargaHorariaPIDMBean.listaChAdmin)}" />
							 
							<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaChAdmin}" varStatus="status">
								<c:if test="${ fn:length(cargaHorariaPIDMBean.listaChAdmin) > 1 }">

									<c:if test="${status.count < tmp}">
										${item.atividade.descricao}, 
									</c:if>
									<c:if test="${status.count == tmp}">
										${item.atividade.descricao} 
									</c:if>
									
								</c:if>
							  	<c:if test="${ fn:length(cargaHorariaPIDMBean.listaChAdmin) == 1 }">
									${item.atividade.descricao}
								</c:if>
							</c:forEach>
							<c:if test="${ empty cargaHorariaPIDMBean.listaChAdmin }">
								Nenhuma designação encontrada.
							</c:if> 
						</td>
						
						<td align="right">
								<a4j:region> 
									<h:inputText value="#{cargaHorariaPIDMBean.obj.percentualAdministracao}" maxlength="3" size="3" id="chAtendAdmin" title="Funções Administrativas" onkeyup="return formatarInteiro(this);">
										<a4j:support
										reRender="outputChHoraAdmin, outputChOutrasAtiv, outputResumo, listagemCheckBoxAdministracao, legenda" 
										actionListener="#{cargaHorariaPIDMBean.calcularHorarioPorPercentualAdministracao}" event="onblur"></a4j:support>
									</h:inputText>%
								</a4j:region>
						</td>
						
						<td align="right">
							<a4j:outputPanel id="outputChHoraAdmin">
								<h:outputText value="#{cargaHorariaPIDMBean.obj.chTotalAdministracao}" id="chAtendAdminHorario">
									<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
								</h:outputText>h
							</a4j:outputPanel>
						</td>
					</tr>
					
					<tr>
						<td>Outras atividades Desenvolvidas em Cursos de Graduação e pós-graduação e/ou outros projetos institucionais com remuneração específica, mediante autorização do CONSEPE</td>
						<td></td>
						
						<td align="right">					
							<a4j:region> 
								<h:inputText value="#{cargaHorariaPIDMBean.obj.chOutrasAtividades.percentualOutrasAtividades}" maxlength="3" size="3" id="chOutrasAtiv" title="Outras Atividades" onkeyup="return formatarInteiro(this);">
									<a4j:support
									reRender="outputOutrasAtiv, outputChOutrasAtiv, outputResumo, legenda" 
									actionListener="#{cargaHorariaPIDMBean.calcularHorarioPorPercentualOutrasAtividades}" event="onblur"></a4j:support>
								</h:inputText>%
							</a4j:region>
						</td>
						
						<td align="right">
							<a4j:outputPanel id="outputOutrasAtiv"> 
								<h:outputText value="#{cargaHorariaPIDMBean.obj.chOutrasAtividades.chSemanalOutrasAtividades}" id="chAtendOutrasAtivHorario">
									<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
								</h:outputText>h
							</a4j:outputPanel>
						</td>
					</tr>
					
				</table>

				<a4j:outputPanel id="outputChOutrasAtiv" styleClass="quadroTotais">
					<h:outputText value="TOTAL DE PESQUISA, EXTENSÃO E OUTRAS ATIVIDADES:"/> 
					<span class="total"> 
					<h:outputText value="#{cargaHorariaPIDMBean.obj.totalGrupoOutrasAtividades}">
						<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
					</h:outputText> <h:outputText value="h"/>
					</span>
				</a4j:outputPanel>
						
			</rich:panel>
			
			<div align="center">

			<rich:panel header="Especifique abaixo selecionando as outras atividades que desenvolve:" style="width: 100%;" id="ativComplementares">
				<a4j:outputPanel id="legenda">
					<c:if test="${not empty cargaHorariaPIDMBean.listaGrupoEnsino
					 	|| not empty cargaHorariaPIDMBean.listaGrupoAtivPesqProducaTec
						|| not empty cargaHorariaPIDMBean.listaGrupoAtivExtensao
						|| not empty cargaHorariaPIDMBean.listaGrupoAtivAdmin}">
						<div class="infoAltRem" style="width:80%">
							<h:graphicImage value="/img/biblioteca/cima.gif"style="overflow: visible;"/>: Adicionar observação
							<h:graphicImage value="/img/biblioteca/baixo.gif"style="overflow: visible;"/> : Remover observação
						</div>
					</c:if>
				</a4j:outputPanel>
				<div align="left">
					<div><b>Outras atividades de Ensino</b></div>
				
					<a4j:outputPanel id="listagemCheckBoxEnsino">
						<c:if test="${empty cargaHorariaPIDMBean.listaGrupoEnsino}"> 
							Para selecionar atividades dessa seção você precisa informar um percentual em Atividades de Ensino.
						</c:if>
						<c:if test="${not empty cargaHorariaPIDMBean.listaGrupoEnsino}">
			
						<table class="opcoesAtividades">
							<tr>
								<td></td>
								<td></td>
								<td></td>
							</tr>
								<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaGrupoEnsino}">
									<tr>
										<td>
											<h:selectBooleanCheckbox value="#{item.selecionada}" id="checkBoxEnsino" />	
										</td>
										
										<td>
											<h:outputText value="#{item.denominacao}"/>
												
											<div id="linha_${item.id}" style="display: ${not empty item.observacao ? "table-cell" : "none"};">
												<h:inputTextarea id="linha_text_area_${item.id}" value="#{item.observacao}" cols="120" rows="2"  
												onkeyup="this.value = this.value.substring(0, 200);" />
											</div>
									
										</td>
										<t:htmlTag value="td" style="text-align: right;">
											<c:if test="${not empty item.observacao}">
												<a id="linha_cima_${item.id}" style="display: inline;" href="javascript: void(0);" onclick="esconderDetalhes(${item.id});" title="Remover observação">
													<img src="${ctx}/img/biblioteca/cima.gif" />
													<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
												</a>
												<a id="linha_baixo_${item.id}" style="display: none;" href="javascript: void(0);" onclick="habilitarDetalhes(${item.id});" title="Adicionar observação">
													<img src="${ctx}/img/biblioteca/baixo.gif" />
													<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
												</a>
											</c:if>											
											<c:if test="${empty item.observacao}">
												<a id="linha_baixo_${item.id}" style="display: inline;" href="javascript: void(0);" onclick="habilitarDetalhes(${item.id});" title="Adicionar observação">
													<img src="${ctx}/img/biblioteca/baixo.gif" />
													<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
												</a>
												<a id="linha_cima_${item.id}" style="display: none;" href="javascript: void(0);" onclick="esconderDetalhes(${item.id});" title="Remover observação">
													<img src="${ctx}/img/biblioteca/cima.gif" />
													<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
												</a>
											</c:if>
										</t:htmlTag>
									</tr>
								</c:forEach>
							</table>

						</c:if>
					</a4j:outputPanel>
				</div>
				
				<br>
				<div align="left">
					<div><b>Pesquisa e produção técnica científica</b></div>
				
					<a4j:outputPanel id="listagemCheckBoxPesquisa">
						<c:if test="${empty cargaHorariaPIDMBean.listaGrupoAtivPesqProducaTec}">
							Para selecionar atividades dessa seção você precisa informar um percentual em Atividades de Pesquisa.
						</c:if>
						<c:if test="${not empty cargaHorariaPIDMBean.listaGrupoAtivPesqProducaTec}">
		
							<table class="opcoesAtividades">
							<tr>
								<td></td>
								<td></td>
								<td></td>
							</tr>
								<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaGrupoAtivPesqProducaTec}">
									<tr>
										<td>
											<h:selectBooleanCheckbox value="#{item.selecionada}" id="checkBoxPesquisa" />	
										</td>
										
										<td>
											<h:outputText value="#{item.denominacao}"/>
												
											<div id="linha_${item.id}" style="display: ${not empty item.observacao ? "table-cell" : "none"};">
												<h:inputTextarea id="linha_text_area_${item.id}" value="#{item.observacao}" cols="120" rows="2"  
												onkeyup="this.value = this.value.substring(0, 200);" />
											</div>
									
										</td>
										<t:htmlTag value="td" style="text-align: right;">
												<c:if test="${not empty item.observacao}">
													<a id="linha_cima_${item.id}" style="display: inline;" href="javascript: void(0);" onclick="esconderDetalhes(${item.id});" title="Remover observação">
														<img src="${ctx}/img/biblioteca/cima.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
													<a id="linha_baixo_${item.id}" style="display: none;" href="javascript: void(0);" onclick="habilitarDetalhes(${item.id});" title="Adicionar observação">
														<img src="${ctx}/img/biblioteca/baixo.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
												</c:if>											
												<c:if test="${empty item.observacao}">
													<a id="linha_baixo_${item.id}" style="display: inline;" href="javascript: void(0);" onclick="habilitarDetalhes(${item.id});" title="Adicionar observação">
														<img src="${ctx}/img/biblioteca/baixo.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
													<a id="linha_cima_${item.id}" style="display: none;" href="javascript: void(0);" onclick="esconderDetalhes(${item.id});" title="Remover observação">
														<img src="${ctx}/img/biblioteca/cima.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
												</c:if>
										</t:htmlTag>
									</tr>
								</c:forEach>
							</table>
							
						</c:if>
					</a4j:outputPanel>
				</div>
				
				<br>
				
				<div align="left">
					<div><b>Extensão ou outras atividades técnicas</b></div>
				
					<a4j:outputPanel id="listagemCheckBoxExtensaoOutrasAtividTecnicas">
						<c:if test="${empty cargaHorariaPIDMBean.listaGrupoAtivExtensao}">
							Para selecionar atividades dessa seção você precisa informar um percentual em Atividades de Extensão ou Outras atividades técnicas.
						</c:if>
						<c:if test="${not empty cargaHorariaPIDMBean.listaGrupoAtivExtensao}">
						
						<table class="opcoesAtividades">
							<tr>
								<td></td>
								<td></td>
								<td></td>
							</tr>
								<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaGrupoAtivExtensao}">
									<tr>
										<td>
											<h:selectBooleanCheckbox value="#{item.selecionada}" id="checkBoxExtensao" />	
										</td>
										
										<td>
											<h:outputText value="#{item.denominacao}"/>
												
											<div id="linha_${item.id}" style="display: ${not empty item.observacao ? "table-cell" : "none"};">
												<h:inputTextarea id="linha_text_area_${item.id}" value="#{item.observacao}" cols="120" rows="2" 
												onkeyup="this.value = this.value.substring(0, 200);" />
											</div>
									
										</td>
										<t:htmlTag value="td" style="text-align: right;">
												<c:if test="${not empty item.observacao}">
													<a id="linha_cima_${item.id}" style="display: inline;" href="javascript: void(0);" onclick="esconderDetalhes(${item.id});" title="Remover observação">
														<img src="${ctx}/img/biblioteca/cima.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
													<a id="linha_baixo_${item.id}" style="display: none;" href="javascript: void(0);" onclick="habilitarDetalhes(${item.id});" title="Adicionar observação">
														<img src="${ctx}/img/biblioteca/baixo.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
												</c:if>											
												<c:if test="${empty item.observacao}">
													<a id="linha_baixo_${item.id}" style="display: inline;" href="javascript: void(0);" onclick="habilitarDetalhes(${item.id});" title="Adicionar observação">
														<img src="${ctx}/img/biblioteca/baixo.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
													<a id="linha_cima_${item.id}" style="display: none;" href="javascript: void(0);" onclick="esconderDetalhes(${item.id});" title="Remover observação">
														<img src="${ctx}/img/biblioteca/cima.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
												</c:if>
										</t:htmlTag>
									</tr>
								</c:forEach>
							</table>
						
						</c:if>
					</a4j:outputPanel>
				</div>
				
				<br>
				
				<div align="left">
					<div><b>Administração</b></div>
				
					<a4j:outputPanel id="listagemCheckBoxAdministracao">
						<c:if test="${empty cargaHorariaPIDMBean.listaGrupoAtivAdmin}">
							Para selecionar atividades dessa seção você precisa informar um percentual em Atividades de Administração.
						</c:if>
						
						<c:if test="${not empty cargaHorariaPIDMBean.listaGrupoAtivAdmin}">
						
							<table class="opcoesAtividades">
							<tr>
								<td></td>
								<td></td>
								<td></td>
							</tr>
								<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaGrupoAtivAdmin}">
									<tr>
										<td>
											<h:selectBooleanCheckbox value="#{item.selecionada}" id="checkBoxAdmin" />	
										</td>
										
										<td>
											<h:outputText value="#{item.denominacao}"/>
												
											<div id="linha_${item.id}" style="display: ${not empty item.observacao ? "table-cell" : "none"};">
												<h:inputTextarea id="linha_text_area_${item.id}" value="#{item.observacao}" cols="120" rows="2"  
												onkeyup="this.value = this.value.substring(0, 200);" />
											</div>
									
										</td>
										<t:htmlTag value="td" style="text-align: right;">
												<c:if test="${not empty item.observacao}">
													<a id="linha_cima_${item.id}" style="display: inline;" href="javascript: void(0);" onclick="esconderDetalhes(${item.id});" title="Remover observação">
														<img src="${ctx}/img/biblioteca/cima.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
													<a id="linha_baixo_${item.id}" style="display: none;" href="javascript: void(0);" onclick="habilitarDetalhes(${item.id});" title="Adicionar observação">
														<img src="${ctx}/img/biblioteca/baixo.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
												</c:if>											
												<c:if test="${empty item.observacao}">
													<a id="linha_baixo_${item.id}" style="display: inline;" href="javascript: void(0);" onclick="habilitarDetalhes(${item.id});" title="Adicionar observação">
														<img src="${ctx}/img/biblioteca/baixo.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
													<a id="linha_cima_${item.id}" style="display: none;" href="javascript: void(0);" onclick="esconderDetalhes(${item.id});" title="Remover observação">
														<img src="${ctx}/img/biblioteca/cima.gif" />
														<h:graphicImage value="/img/biblioteca/cima.gif" style="display: none;" />
													</a>
												</c:if>
										</t:htmlTag>
									</tr>
								</c:forEach>
							</table>
						
						</c:if>
					</a4j:outputPanel>
				</div>
					
			</rich:panel>