<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js">

</script>
<script>
/* Máscaras ER */  
function mascara(o,f){  
    v_obj=o  
    v_fun=f  
    setTimeout("execmascara()",1)  
}  
function execmascara(){  
    v_obj.value=v_fun(v_obj.value)  
}  
function mtel(v){  
    v=v.replace(/\D/g,"");             //Remove tudo o que não é dígito  
    v=v.replace(/(\d)(\d{4})$/,"$1-$2");    //Coloca hífen entre o quarto e o quinto dígitos  
    return v;  
}  
</script>

<h2><ufrn:subSistema /> > Cadastro Único de Bolsistas > Questionário</h2>

<f:view>
	<h:form id="form">
	<h:messages></h:messages>
	<table class="formulario" style="width: 85%;">
		<caption>Questionário Sócio Econômico</caption>
			<tbody>
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
				<tr>
					<td colspan="7" class="subFormulario">Pontuação</td>
				</tr>			
				
				<tr>
					<th>Pontos:</th>
					<td colspan="6">${adesaoCadastroUnico.obj.pontuacao}</td>
				</tr>
				</ufrn:checkRole>
				<tr>
					<td colspan="7" class="subFormulario">Endereço da Família</td>
				</tr>
			
				<tr class="linhaCep">
					<th>CEP: <span class="required"></span></th>
					<td colspan="5">
						<h:inputText value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.cep}"
							maxlength="10" size="10"
							id="endCEP" onblur="formataCEP(this, event, null); ConsultadorCep.consultar(); "/>
							
						<a href="javascript://nop/" onclick="ConsultadorCep.consultar(); $('form:ufEnd').onChange();">
							<img src="/sigaa/img/buscar.gif"/>
						</a>
						
						<span class="info">(clique na lupa para buscar o endereço do CEP informado)</span>
						<span id="cepIndicator" style="display: none;">
							<img src="/sigaa/img/indicator.gif"/> Buscando endereço...
						</span>
					</td>
				</tr>
				
				<tr>
					<th>Logradouro: <span class="required"></span></th>
					<td colspan="4">
						
							<h:selectOneMenu value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.tipoLogradouro.id}" id="tipoLogradouro">
								<f:selectItems value="#{tipoLogradouro.allCombo}" />
							</h:selectOneMenu>
							
							<h:inputText value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.logradouro }"
								maxlength="50" id="logradouro" size="70"/>
					</td>
					
						<th>N.&deg;:</th>
						<td>
							<h:inputText value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.numero}" maxlength="8" size="6" id="endNumero" />
						</td>	
				</tr>
		
				<tr>
					<th>Bairro: <span class="required"></span></th>
						<td>
							<h:inputText value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.bairro}" maxlength="20" size="20" id="endBairro"/>
						</td>
						
						<th>Complemento:</th>
						<td>
							<h:inputText value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.complemento}" maxlength="40" size="20" id="endComplemento" />
						</td>
				</tr>
				<tr>
					<th class="required">UF:</th>
					<td>
						<h:selectOneMenu value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.unidadeFederativa.id}" id="ufEnd"
										valueChangeListener="#{adesaoCadastroUnico.carregarMunicipios }">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
							<a4j:support event="onchange" reRender="endMunicipio" />
						</h:selectOneMenu>
					</td>
					
					<th class="required">Município:</th>
					<td colspan="4">
						<h:selectOneMenu value="#{adesaoCadastroUnico.obj.contatoFamilia.endereco.municipio.id}" id="endMunicipio">
							<f:selectItems value="#{adesaoCadastroUnico.municipiosEndereco}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Tel. Fixo:</th>
		
					<td><h:inputText disabled="#{dadosPessoais.readOnly}" 
						value="#{adesaoCadastroUnico.obj.contatoFamilia.telefone}" maxlength="9" size="9" id="telFixoNumero" onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" />(somente números)</td>
						
					<th>Tel. Celular:</th>
					<td colspan="3"><h:inputText id="telCelNumero" disabled="#{dadosPessoais.readOnly}"
						value="#{adesaoCadastroUnico.obj.contatoFamilia.celular}" maxlength="10" size="10" onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" />(somente números)</td>
				</tr>
			
				<tr>
					<td colspan="7" class="subFormulario">Respostas</td>
				</tr>			
				<tr>
					<td colspan="7"><%@include file="/geral/questionario/_formulario_respostas.jsp" %></td>
				<%-- 
					<td colspan="7"><%@include file="/geral/questionario/_respostas.jsp" %></td>
					--%>
				</tr>
				<tr>
					<td colspan="7" class="subFormulario">Itens do conforto familiar</td>
				</tr>							
				<tr>
					<td colspan="7">
						<t:newspaperTable value="#{adesaoCadastroUnico.obj.listaConfortoFamiliar}" var="confortoFamiliar" newspaperColumns="2" 
									newspaperOrientation="horizontal" rowClasses="linhaPar, linhaImpar"  width="100%">					
					
							<t:column style="text-align: right;">
			                      <h:outputText value="#{confortoFamiliar.item.item}" />
		                    </t:column>
		                    
		                    <t:column>
								<h:selectOneMenu value="#{confortoFamiliar.quantidade}">
									<f:selectItem itemValue="0" itemLabel="Nenhum" />
									<f:selectItem itemValue="1" itemLabel="1" />
									<f:selectItem itemValue="2" itemLabel="2" />
									<f:selectItem itemValue="3" itemLabel="3" />
								</h:selectOneMenu>
		                    </t:column>
		                </t:newspaperTable>
					</td>				
				</tr>				
				
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="7">
						<h:commandButton value="Alterar" action="#{adesaoCadastroUnico.alterarAdesaoDiscente}" /> 
						<h:commandButton value="Cancelar" action="#{adesaoCadastroUnico.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>			
			</tfoot>			
	</table>
	
	
		
	</h:form>
<script type="text/javascript">
ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro', 'form:endBairro', 'form:endMunicipio', 'form:ufEnd' );
</script>		
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>