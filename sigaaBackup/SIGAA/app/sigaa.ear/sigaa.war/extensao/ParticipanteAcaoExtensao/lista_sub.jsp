<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao"%>

<script type="text/javascript">var J = jQuery.noConflict();</script>

<style>
	.aprovada {
		color: #292;		
		font-weight: bold;
	}
	.centralizado {
		text-align: center !important;
	}
</style>
<f:view>
<a4j:keepAlive beanName="participanteAcaoExtensao"></a4j:keepAlive>

<%@include file="/portais/docente/menu_docente.jsp"%>
   <h2><ufrn:subSistema /> &gt; Gerenciar Participantes de Ações de Extensão</h2>
   <div class="descricaoOperacao">
        <b>Atenção:</b> Caro(a) Coordenador(a), a emissão do certificado de cada participante só será autorizada quando:
        <ul>
          <li>O Relatório Final da Ação estiver sido validado pela Pró-Reitoria de Extensão.</li>
          <li>A Ação de Extensão estiver concluída.</li>
          <li>O participante deverá ter frequência satisfatória.</li>
          <li>O participante deverá ter a emissão do certificado autorizada pela Coordenação da ação.</li>          
        </ul>
        <br/>
        <h:graphicImage value="/img/warning.gif" style="overflow: visible; vertical-align: middle" /> Para realizar alterações nos participantes do projeto é necessário selecionar um participante no mínimo e confirmar as alterações.
      		
    </div>

    <br />
	<br />

	


 	<h:form id="frmLista">

	<table class="formulario" width="90%">
	<caption>Busca por Participantes</caption>
	<tbody>  
		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{participanteAcaoExtensao.checkBuscaNome}"  id="selectBuscaNome" />
			</td>
	    	<td> <label> Nome: </label> </td>
			<td>
		
			 <h:inputText id="nome" value="#{ participanteAcaoExtensao.buscaNome }" size="60" onchange="javascript:$('frmLista:selectBuscaNome').checked = true;" onfocus="javascript:$('frmLista:selectBuscaNome').checked = true;"/>
		
			</td>
		</tr>
		<tr>
				<td>
					<h:selectBooleanCheckbox value="#{participanteAcaoExtensao.checkBuscaMunicipio}"  id="selectBuscaMunicipio" />
			</td>
			<td>UF:</td>
			<td>
				<table>		
				<tr>
				<td><h:selectOneMenu value="#{participanteAcaoExtensao.obj.unidadeFederativa.id}" id="uf" immediate="true">
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
						<a4j:support event="onchange" reRender="municipio" action="#{participanteAcaoExtensao.carregarMunicipios}"/>
					</h:selectOneMenu>
				</td>
				<td>Município:</td>
				<td>
					<h:selectOneMenu value="#{participanteAcaoExtensao.obj.municipio.id}" id="municipio" onchange="javascript:$('frmLista:selectBuscaMunicipio').checked = true;" onfocus="javascript:$('frmLista:selectBuscaMunicipio').checked = true;">
					<f:selectItems value="#{participanteAcaoExtensao.municipiosEndereco}" />
					</h:selectOneMenu>
				</td>
				<tr>
			</table>
			</td>
			 
			</tr>
		     
		</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ participanteAcaoExtensao.buscarParticipantesSubAcao }">
			</h:commandButton>
			<h:commandButton value="Cancelar" action="#{ participanteAcaoExtensao.cancelar }" onclick="#{confirm}" immediate="true"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>
	

	<br />
	
		<div class="infoAltRem">
			
			
			<h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;" />: Visualizar
			
			<c:if test="${acesso.coordenadorExtensao}">
				<h:graphicImage value="/img/pesquisa/view.gif" height="16" width="16" 
						style="overflow: visible;" styleClass="noborder" />: Emitir declaração
				<h:graphicImage value="/img/certificate.png" height="16" width="16" 
						style="overflow: visible;" styleClass="noborder" />: Emitir certificado
				<br/>
			</c:if> 
		</div>
		
		<table class="listagem">
			<caption class="listagem">Lista de Participantes da Mini Ação Selecionada</caption>
			<thead>
				<tr>
				    <th><h:selectBooleanCheckbox value="false" onclick="checkAll(this)"/></th>
					<th>CPF</th>
					<th>Passaporte</th>
					<th>Nome</th>
					<th class="centralizado">Participação</th>
					<th class="centralizado">Freq.</th>
					<th class="centralizado">Declaração</th>
					<th class="centralizado">Certificado</th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
					
					
				</tr>
			</thead>

			<tbody>
				<tr>
					<td class="subFormulario" colspan="15">${participanteAcaoExtensao.subAcao.atividade.codigoTitulo} - ${participanteAcaoExtensao.subAcao.titulo} </td>
				</tr>
					
				<c:forEach items="#{participanteAcaoExtensao.resultadosBusca}" var="participante" varStatus="status">
					
				      <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				        <td><h:selectBooleanCheckbox value="#{participante.selecionado}" styleClass="check check_#{participante.id}"/></td>
						<c:if test="${participante.cpf <= 0 || participante.cpf == null}">
							<td> - </td>
						</c:if>
						<c:if test="${participante.cpf > 0}">
							<td><ufrn:format type="cpf_cnpj" valor="${participante.cpf}" /></td>
						</c:if>
						<c:if test="${participante.passaporte == null}">
							<td> - </td>
						</c:if>
						<c:if test="${participante.passaporte != null}">
							<td>${participante.passaporte}</td>
						</c:if>
						<td>${participante.nome}</td>
						<td class="centralizado">${participante.tipoParticipacao.descricao}</td>
						<td class="centralizado">
							<h:inputText value="#{participante.frequencia}" maxlength="3" size="3" onkeyup="formatarInteiro(this);" 
									onblur="javascript: if(this.value && this.value.valueOf()>100) this.value = '100';" converter="#{ intConverter }"
									onchange="javascript:document.getElementsByClassName('check_#{participante.id}')[0].checked = true;"/>
							<b>%</b>
						</td>
												
						<td>
                                 <h:selectOneMenu value="#{participante.autorizacaoDeclaracao}" id="declaracaoAutorizada" 
                                 	rendered="#{acesso.coordenadorExtensao && participante != null   }" 
                                 	onchange="javascript:document.getElementsByClassName('check_#{participante.id}')[0].checked = true;">
                                     <f:selectItem itemValue="true" itemLabel="SIM" />
                                     <f:selectItem itemValue="false" itemLabel="NÃO" />
                                 </h:selectOneMenu>
                        </td>
                        
                        <td>
                                 <h:selectOneMenu value="#{participante.autorizacaoCertificado}" id="certificadoAutorizado" 
                                 	rendered="#{acesso.coordenadorExtensao && participante != null   }"
                                 	onchange="javascript:document.getElementsByClassName('check_#{participante.id}')[0].checked = true;">
                                     <f:selectItem itemValue="true" itemLabel="SIM" />
                                     <f:selectItem itemValue="false" itemLabel="NÃO" />
                                 </h:selectOneMenu>
                        </td>
						
						
						
						<td width="2%">
							<h:commandLink title="Visualizar" action="#{participanteAcaoExtensao.view}" 
									style="border: 0;" id="visualizar">
								<f:param name="id" value="#{participante.id}" />
								<h:graphicImage url="/img/extensao/user1_view.png" />
							</h:commandLink>
						</td>
						
						<td width="2%">
							<h:commandLink title="Emitir declaração" style="border: 0;" id="emitirDeclaracao"
									action="#{declaracaoParticipanteExtensao.emitirDeclaracao}" rendered="#{ participante.passivelEmissaoDeclaracao }">
								<f:setPropertyActionListener target="#{declaracaoParticipanteExtensao.participante.id}" 
										value="#{participante.id}" />
					    	    <h:graphicImage url="/img/pesquisa/view.gif" height="16" width="16" />
							</h:commandLink>
						</td>
							
						<td width="2%">
							<h:commandLink title="Emitir certificado" style="border: 0;" id="emitirCertificado"
									action="#{certificadoParticipanteExtensao.emitirCertificado}" rendered="#{ participante.passivelEmissaoCertificado }" >
								<f:setPropertyActionListener target="#{certificadoParticipanteExtensao.participante.id}" 
										value="#{participante.id}" />
				            	<h:graphicImage url="/img/certificate.png" height="16" width="16" />
							</h:commandLink>
						</td>
						  
						
					</tr>
					
				</c:forEach>
				
				<c:if test="${empty participanteAcaoExtensao.resultadosBusca}">
					<tr>
						<td colspan="15">
							<center><i>Não há participantes cadastrados</i></center>
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="15" align="center">
						<h:commandButton value="Confirmar Alterações" id="btnConfirmarAlteracoes" action="#{participanteAcaoExtensao.atualizarParticipacoesSubAtividade}" />						
						<h:commandButton value="<< Voltar" action="#{participanteAcaoExtensao.listarAcoesExtensao}" id="btVoltar"  immediate="true"/>						
						<h:commandButton value="Cancelar" id="btcancelar" action="#{participanteAcaoExtensao.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		
			<br />
			<div style="text-align: center;">
				<h:commandButton
					image="/img/voltar.gif" actionListener="#{paginacao.previousPage}"
					rendered="#{paginacao.paginaAtual > 0 }"
					style="vertical-align:middle" id="paginacaoVoltar" /> 
					
				<%--<h:selectOneMenu value="#{paginacao.paginaAtual}"
					valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true" id="mudaPagina">
					<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}" />
					<a4j:support  event="onchange" actionListener="#{participanteAcaoExtensao.changePage}" />
				</h:selectOneMenu> --%>
				
				<b><h:outputText value="#{paginacao.paginaAtual +1} de #{paginacao.totalPaginas}"></h:outputText></b>
				
				<h:commandButton image="/img/avancar.gif"
				actionListener="#{paginacao.nextPage}"
				rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"
				style="vertical-align:middle" id="paginacaoAvancar" /> <br />
			<br />
			<em><h:outputText value="#{paginacao.totalRegistros }" />
			Registro(s) Encontrado(s)</em></div>
	</h:form>
	
	
	
</f:view>

  <script type="text/javascript">
	var posProcessamento = function() {
		$('form:municipio').value = $('form:municipio').options[0].value;
		$('form:uf').onchange();
	}
	
</script>

<script type="text/javascript">
function checkAll(elem) {
    $A(document.getElementsByClassName('check')).each(function(e) {
        e.checked = elem.checked;
    });
}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>