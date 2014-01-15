<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/portais/discente/menu_discente.jsp"%>
<style>
	#abas-relatorio div.yui-ext-tabitembody{
		background: #EAF3FD;
		padding: 5px 15px;
	}

	#abas-relatorio textarea {
		width: 98%;
		margin: 0 auto;
	}

	p.descricao {
		padding: 5px 100px 10px;
		font-style: italic;
		text-align: center;
	}

</style>

<h2>	
	<ufrn:subSistema /> > Requerimento Padrão
</h2>
<div id="operacaoAjuda" class="descricaoOperacao" style="display:none"><a style="color: rgb(170, 170, 170); font-size: 0.9em;" onclick="$('operacaoAjuda').hide();$('ajuda').show();" href="javascript://nop/">  (^) mostrar ajuda </a></div>
<div id="ajuda" class="descricaoOperacao">
	<p>
		<strong>Bem-vindo ao Cadastro de Requerimento.</strong>
	</p>
	<br/>
	<p>
		O requerimento padrão eletrônico é uma maneira de agilizar seu atendimento no ${ configSistema['siglaUnidadeGestoraGraduacao'] }. Cadastre o requerimento pela Internet, imprima-o e formalize-o no Protocolo do DAE. Assim você evita as filas e o tempo de espera de atendimento e escrita do requerimento in loco.
	</p>
	<br/>
	<p>
		Qual a diferença entre <em>Apenas Gravar</em> e <em>Gravar e Enviar</em>?
	</p>
	<br/>
	<p>
		<strong><em>Apenas Gravar:</em></strong> Salva o que você digitou até o momento, mas não submete. É apenas um rascunho. Sendo assim, você pode terminar o seu requerimento em um outro momento, podendo parar e continuar quantas vezes for necessário.
	</p>
	<br/>
	<p>
		<strong><em>Gravar e Enviar:</em></strong> Envia o requerimento para o ${ configSistema['siglaUnidadeGestoraGraduacao'] }. Depois desta etapa voce não poderá mais editar o texto. Use esta opção somente quando seu requerimento estiver pronto.
	</p>
	<br/>
	<a style="color: rgb(170, 170, 170); font-size: 0.9em;" onclick="$('ajuda').hide();$('operacaoAjuda').show();" href="javascript://nop/">  (x) fechar ajuda </a>
</div>

<h:form id="form">

    <table class="formulario" width="95%">
		<caption>Preencha os campos do requerimento</caption>
   	    <tbody>
						
			<c:if test="${ not empty requerimento.obj.dataAtualizado }">
			<tr>
				<th nowrap="nowrap"> <b>Última alteração em:</b> </th>
				<td>
					<fmt:formatDate value="${requerimento.obj.dataAtualizado}" pattern="dd/MM/yyyy 'às' HH:mm:ss" />
				</td>
			</tr>
			</c:if>
			<tr>
				<td colspan="2">
				<div id="abas-requerimento">
					<div id="pessoal-relatorio">
						<p class="descricao">
							Confira seus dados pessoais. Caso exista alguma informação incorreta ou desatualizada procure o ${ configSistema['siglaUnidadeGestoraGraduacao'] }.
						</p>
						<table>
							<tr>
								<td align="right"><strong>Matrícula:</strong></td>
								<td>${requerimento.obj.discente.matricula}</td>
							</tr>
							
							<tr>
								<td align="right"><strong>Nome:</strong></td>
								<td>${requerimento.obj.discente.nome}</td>
							</tr>
							
							<tr>
								<td align="right"><strong>CPF:</strong></td>
								<td>${requerimento.obj.discente.pessoa.cpf_cnpj}</td>
							</tr>							
							
							<tr>
								<td align="right"><strong>Curso:</strong></td>
								<td>${requerimento.obj.discente.curso}</td>
							</tr>
							
							<tr>
								<td align="right"><strong>Endereço:</strong></td>
								<td>${requerimento.obj.discente.pessoa.enderecoContato.descricao}</td>
								
								<td align="right"><strong>Bairro:</strong></td>
								<td>${requerimento.obj.discente.pessoa.enderecoContato.bairro}</td>								
							</tr>							
							
							<tr>
								<td align="right"><strong>Cidade/UF:</strong></td>
								<td>${requerimento.obj.discente.pessoa.enderecoContato.municipio}/${requerimento.obj.discente.pessoa.enderecoContato.unidadeFederativa.sigla}</td>
							</tr>														
							
							<tr>
								<td align="right"><strong>Telefone:</strong></td>
								<td>${requerimento.obj.discente.pessoa.telefone}</td>
							</tr>								
							
							<tr>
								<td align="right"></td>
								<td>${requerimento.obj.discente.pessoa.celular}</td>
							</tr>															
							
							<tr>
								<td align="right"><strong>Email:</strong></td>
								<td>
									<c:choose>
										<c:when test="${requerimento.obj.discente.pessoa.email != null}">
											${fn:toUpperCase(requerimento.obj.discente.pessoa.email)} 
										</c:when>
										
										<c:otherwise>
											<i>Não possui email cadastrado.</i> 
										</c:otherwise>
									</c:choose>
								</td>
							</tr>																						
						</table>
					</div>
										
				</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div id="abas-informacao">
										
						<div id="requerimento-detalhe">
							<c:if test="${requerimento.obj.tipo.id == 0}">
								<p class="descricao">
									Selecione o tipo de solicitação. 
								</p>
								<center>	
									<p>
										<h:selectOneMenu id="tipoRequerimento" value="#{requerimento.obj.tipo.id}">
											<f:selectItems value="#{requerimento.allCombo}"/>
										</h:selectOneMenu>
									</p>
									<br />	
								</center>
							</c:if>
							<p class="descricao">
								 Informe a justificativa de forma clara e objetiva.
							</p>
							<p align="center">
								<h:inputTextarea id="solicitacao" rows="9" style="width: 95%"  value="#{requerimento.obj.solicitacao}"></h:inputTextarea>
							</p>
							
							<c:if test="${requerimento.obj.tipo.id == 4}">
								<div id="trancar">
									<p class="descricao">
										<span class="required">&nbsp;</span>
										A partir de qual Ano-Periodo? <h:inputText id="ano" value="#{requerimento.obj.anoBase}" size="4" maxlength="4" />-<h:inputText id="periodo" value="#{requerimento.obj.periodoBase}" size="1" maxlength="1" />										
										<br/>
										<br/>
										<span class="required">&nbsp;</span>
										Quantos semestres deseja trancar? 
										<h:inputText id="qtdSemestres" value="#{requerimento.obj.trancarQtdSemestres}" size="2" maxlength="2"></h:inputText>
										
										<p class="descricao" style="color: grey">
											Ex: Ano-Periodo: 2008.2 e Quantidade: 2, será solicitado: 2008.2 e 2009.1
										</p>
										
									</p>
								</div>
							</c:if>
						</div>
					</div>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="cancelar" value="Cancelar"  action="#{requerimento.cancelar}" immediate="true"/>
					<h:commandButton id="enviar" value="Apenas Gravar (Rascunho)" action="#{requerimento.gravarRequerimento}" />
					<h:commandButton id="gravar" value="Gravar e Enviar" action="#{requerimento.enviarRequerimento}" />
	    		</td>
	    	</tr>
		</tfoot>
	</table>
</h:form>



<script type="text/javascript">
var Tabs = {
    init : function(){
        var tabs = new YAHOO.ext.TabPanel('abas-requerimento');
	   	tabs.addTab('pessoal-relatorio', "Requerimento Padrão");
		tabs.activate('pessoal-relatorio');
		
		var tabs = new YAHOO.ext.TabPanel('abas-informacao');
		tabs.addTab('requerimento-detalhe', "Detalhes da Solicitacao");
		tabs.activate('requerimento-detalhe');
	}
	
	
	
}
YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>