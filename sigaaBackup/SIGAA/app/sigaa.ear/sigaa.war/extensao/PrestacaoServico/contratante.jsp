<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<f:verbatim>
	<script type="text/javascript">
		function esconder(obj) {

				if (obj.value == 'publica'){
					document.getElementById( 'publica' ).style.display = "";
					document.getElementById( 'privada' ).style.display = "none";
					document.getElementById( 'fisica' ).style.display = "none";
				}

				if (obj.value == 'privada'){
					document.getElementById( 'privada' ).style.display = "";
					document.getElementById( 'publica' ).style.display = "none";
					document.getElementById( 'fisica' ).style.display = "none";
				}

				if (obj.value == 'fisica'){
					document.getElementById( 'fisica' ).style.display = "";
					document.getElementById( 'privada' ).style.display = "none";
					document.getElementById( 'publica' ).style.display = "none";
				}
				
			
		}
		
	</script>
</f:verbatim>

<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Dados da Prestação de Serviços</h2>
<br>

<h:form id="form">

<table class=formulario width="100%" border="1">

	<caption class="listagem">Dados da Prestação de Serviços</caption>
	<h:inputHidden value="#{prestacaoServico.obj.id}"/>
	
	
	<tr>
		<td>
			<b> Título do Projeto:</b><br>
			<h:inputText id="titulo" value="#{prestacaoServico.obj.titulo}" readonly="#{prestacaoServico.readOnly}" size="120"/>
		</td>
	</tr>



	<tr>
		<td>
			<b>Tipo de Contratante:</b><br/>
			
			<h:selectOneRadio value="#{prestacaoServico.tipoPessoa}" onclick="javascript:esconder(this);">
			  	<f:selectItem itemLabel="Pessoa Jurídica de Direito Público" itemValue="publica" />
			  	<f:selectItem itemLabel="Pessoa Jurídica de Direito Privado" itemValue="privada" />
			  	<f:selectItem itemLabel="Pessoa Física" itemValue="fisica" />
			</h:selectOneRadio>
			
		</td>
	</tr>
	
	
	<tr>
	  <td>
	  
	  
		  <table width="100%" border="1" id="publica">	  
				
				<tr>
					<td colspan="4">
						<caption class="listagem">Pessoa Pública de Direito Público</caption>
					</td>
				</tr>
				
				<tr>
					<td colspan="4">
						<b> CNPJ:</b><br>
						<h:inputText size="20" id="pjcontratante_cnpj" value="#{prestacaoServico.obj.pessoaJuridicaContratante.pessoa.cpf_cnpj}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
		
		
				<tr>
					<td colspan="4">
						<b> Nome da Entidade:</b><br>
						<h:inputText size="110" id="pjcontratante_nome" value="#{prestacaoServico.obj.pessoaJuridicaContratante.pessoa.nome}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					
				</tr>
			
				<tr>
					<td colspan="3">
						<b> Endereco:</b><br>
						<h:inputText size="70" id="pjcontratante_endereco_rua" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.logradouro}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					<td>
						<b> Número:</b><br>
						<h:inputText id="pjcontratante_endereco_numero" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.numero}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
			
			
				<tr>
					<td colspan="4">
						<b> Complemento:</b><br>
						<h:inputText size="110" id="pjcontratante_endereco_complemento" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.complemento}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
				
				
				<tr>
					<td colspan="3">
						<b> Bairro:</b><br>
						<h:inputText size="70" id="pjcontratante_endereco_bairro" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.bairro}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					<td>
						<b> CEP:</b><br>
						<h:inputText size="12" id="pjcontratante_endereco_cep" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.cep}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
				
				<tr>
					<td colspan="3">
						<b> Cidade:</b><br>
						<h:inputText size="70" id="pjcontratante_endereco_cidade" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.municipio.nome}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					<td>
						<b> UF:</b><br>
						<h:inputText size="3" id="pjcontratante_endereco_uf" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.municipio.unidadeFederativa.sigla}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
				
				
				<tr>
					<td colspan="4">
						<b> Telefone:</b><br>
						<h:inputText size="20" id="pjcontratante_telefone" value="#{prestacaoServico.obj.pessoaJuridicaContratante.pessoa.telefone}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
		
		
				<tr>
					<td colspan="3">
						<b> Nome do Dirigente:</b><br>
						<h:inputText size="50" id="pjcontratante_nomeDirigente" value="#{prestacaoServico.obj.pessoaJuridicaContratante.nomeDirigente}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					<td>
						<b> Cargo do Dirigente:</b><br>
						<h:inputText size="40" id="pjcontratante_cargoDirigente" value="#{prestacaoServico.obj.pessoaJuridicaContratante.cargoDirigente}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
		
	
	
				<tr>
					<td colspan="2">
						<b> Esfera Administrativa:</b><br>
						<h:selectOneMenu id="pjcontratante_esferaAdm" value="#{prestacaoServico.obj.pessoaJuridicaContratante.tipoEsferaAdministrativa.id}" readonly="#{prestacaoServico.readOnly}">
						  <f:selectItems  value="#{prestacaoServico.allTipoEsferaAdministrativaCombo}" />
						</h:selectOneMenu> 						
					</td>
					
					<td colspan="2">
						<b> Outra Esfera Administrativa:</b><br>
						<h:inputText size="40" id="pjcontratante_outraEsferaAdm" value="#{prestacaoServico.obj.pessoaJuridicaContratante.esferaAdministrativaOutra}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
				
			</table>
		


			  <table width="100%" border="1" id="privada" style="display: none">	  


					<tr>
						<td colspan="4">
							<caption class="listagem">Pessoa Pública de Direito Privado</caption>
						</td>
					</tr>



					<tr>
						<td colspan="4">
							<b> CNPJ:</b><br>
							<h:inputText size="20" id="pjcontratante_cnpj" value="#{prestacaoServico.obj.pessoaJuridicaContratante.pessoa.cpf_cnpj}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
			
			
					<tr>
						<td colspan="2">
							<b> Nome Fantasia:</b><br>
							<h:inputText size="55" id="pjcontratante_nome_fantasia" value="#{prestacaoServico.obj.pessoaJuridicaContratante.pessoa.nome}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td colspan="2">
							<b> Razão Social:</b><br>
							<h:inputText size="55" id="pjcontratante_razao_social" value="#{prestacaoServico.obj.pessoaJuridicaContratante.razaoSocial}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
		
				
					<tr>
						<td colspan="3">
							<b> Endereco:</b><br>
							<h:inputText size="70" id="pjcontratante_endereco_rua" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.logradouro}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> Número:</b><br>
							<h:inputText id="pjcontratante_endereco_numero" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.numero}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
				
				
					<tr>
						<td colspan="4">
							<b> Complemento:</b><br>
							<h:inputText size="110" id="pjcontratante_endereco_complemento" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.complemento}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					
					
					<tr>
						<td colspan="3">
							<b> Bairro:</b><br>
							<h:inputText size="70" id="pjcontratante_endereco_bairro" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.bairro}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> CEP:</b><br>
							<h:inputText size="12" id="pjcontratante_endereco_cep" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.cep}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					
					<tr>
						<td colspan="3">
							<b> Cidade:</b><br>
							<h:inputText size="70" id="pjcontratante_endereco_cidade" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.municipio.nome}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> UF:</b><br>
							<h:inputText size="3" id="pjcontratante_endereco_uf" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.municipio.unidadeFederativa.sigla}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					
					
					<tr>
						<td colspan="4">
							<b> Telefone:</b><br>
							<h:inputText size="20" id="pjcontratante_telefone" value="#{prestacaoServico.obj.pessoaJuridicaContratante.pessoa.telefone}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
			
			
					<tr>
						<td colspan="3">
							<b> Nome do Dirigente:</b><br>
							<h:inputText size="50" id="pjcontratante_nomeDirigente" value="#{prestacaoServico.obj.pessoaJuridicaContratante.nomeDirigente}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> Cargo do Dirigente:</b><br>
							<h:inputText size="40" id="pjcontratante_cargoDirigente" value="#{prestacaoServico.obj.pessoaJuridicaContratante.cargoDirigente}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
			
		
					<tr>
						<td colspan="2">
							<b> Natureza:</b><br>
							<h:inputText id="pjcontratante_finsLucrativos" value="#{prestacaoServico.obj.pessoaJuridicaContratante.esferaAdministrativaOutra}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td colspan="2">
							<b> Número do Registro CNSS/MAS:</b><br>
							<h:inputText id="pjcontratante_cnssMas" value="#{prestacaoServico.obj.pessoaJuridicaContratante.registroCnss}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					<tr>
						<td>
							<b> N° Declaração Pública Federal:</b><br>
							<h:inputText id="pjcontratante_declaracaoFederal" value="#{prestacaoServico.obj.pessoaJuridicaContratante.numDeclaracaoFederal}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> N° Declaração Pública Estadual:</b><br>
							<h:inputText id="pjcontratante_declaracaoEstadual" value="#{prestacaoServico.obj.pessoaJuridicaContratante.numDeclaracaoEstadual}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> N° Declaração Pública Municipal:</b><br>
							<h:inputText id="pjcontratante_declaracaoMunicipal" value="#{prestacaoServico.obj.pessoaJuridicaContratante.numDeclaracaoMunicipal}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					
				</table>
				
				
				<table width="100%" border="1" id="fisica" style="display: none">	
					<tr>
						<td colspan="4">
							<caption class="listagem">Pessoa Física</caption>
						</td>
					</tr>
					
					
					<tr>
						<td colspan="4">
							<b> CPF:</b><br>
							<h:inputText size="20" id="pfcontratante_cnpj" value="#{prestacaoServico.obj.pessoaFisicaContratante.cpf_cnpj}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					
					
					<tr>
						<td colspan="4">
							<b> Nome:</b><br>
							<h:inputText size="110" id="pfcontratante_nome" value="#{prestacaoServico.obj.pessoaFisicaContratante.nome}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						
					</tr>
					
					
					
					<tr>
						<td colspan="3">
							<b> Endereco:</b><br>
							<h:inputText size="70" id="pfcontratante_endereco_rua" value="#{prestacaoServico.obj.pessoaFisicaContratante.enderecoContato.logradouro}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> Número:</b><br>
							<h:inputText id="pfcontratante_endereco_numero" value="#{prestacaoServico.obj.pessoaFisicaContratante.enderecoContato.numero}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					
					
					<tr>
						<td colspan="4">
							<b> Complemento:</b><br>
							<h:inputText size="110" id="pfcontratante_endereco_complemento" value="#{prestacaoServico.obj.pessoaFisicaContratante.enderecoContato.complemento}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					
					
					<tr>
						<td colspan="3">
							<b> Bairro:</b><br>
							<h:inputText size="70" id="pfcontratante_endereco_bairro" value="#{prestacaoServico.obj.pessoaFisicaContratante.enderecoContato.bairro}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> CEP:</b><br>
							<h:inputText size="12" id="pfcontratante_endereco_cep" value="#{prestacaoServico.obj.pessoaFisicaContratante.enderecoContato.cep}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					
					<tr>
						<td colspan="3">
							<b> Cidade:</b><br>
							<h:inputText size="70" id="pfcontratante_endereco_cidade" value="#{prestacaoServico.obj.pessoaFisicaContratante.enderecoContato.municipio.nome}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> UF:</b><br>
							<h:inputText size="3" id="pfcontratante_endereco_uf" value="#{prestacaoServico.obj.pessoaFisicaContratante.enderecoContato.municipio.unidadeFederativa.sigla}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
					
					
					<tr>
						
						<td colspan="4">
							<b> Telefone:</b><br>
							<h:inputText size="20" id="pfcontratante_telefone" value="#{prestacaoServico.obj.pessoaFisicaContratante.telefone}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						
					</tr>
					
					
					<tr>
						<td colspan="2">
							<b> Identidade:</b><br>
							<h:inputText size="20" id="pfcontratante_identidade_num" value="#{prestacaoServico.obj.pessoaFisicaContratante.identidade.numero}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						<td>
							<b> Orgão Expedidor:</b><br>
							<h:inputText size="15" id="pfcontratante_identidade_orgao" value="#{prestacaoServico.obj.pessoaFisicaContratante.identidade.orgaoExpedicao}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						
						<td>
							<b> Data Expedição:</b><br>
							<t:inputCalendar id="pfcontratante_identidade_dataExp" value="#{prestacaoServico.obj.pessoaFisicaContratante.identidade.dataExpedicao}"  renderAsPopup="true" renderPopupButtonAsImage="true"	size="12" readonly="#{prestacaoServico.readOnly}"/>
						</td>
						
					</tr>
					<tr>
						<td colspan="4">
							<b> Ação Profissional:</b><br>
							<h:inputText size="100" id="pfcontratante_atividade_profiss" value="#{prestacaoServico.obj.atividadeProfissionalContratante}" readonly="#{prestacaoServico.readOnly}"/>
						</td>
					</tr>
				</table>



		  <table width="100%" border="1" id="orgao">	  
				
				<tr>
					<td colspan="4">
						<caption class="listagem">Identificação do Órgao Administrador Interveniente (Quando Necessário)</caption>
					</td>
				</tr>
				
				<tr>
					<td colspan="4">
						<b> CNPJ:</b><br>
						<h:inputText size="20" id="pjcontratante_cnpj" value="#{prestacaoServico.obj.pessoaJuridicaContratante.pessoa.cpf_cnpj}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
		
		
				<tr>
					<td colspan="4">
						<b> Nome da Entidade:</b><br>
						<h:inputText size="110" id="pjcontratante_nome" value="#{prestacaoServico.obj.pessoaJuridicaContratante.pessoa.nome}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					
				</tr>
			
				<tr>
					<td colspan="3">
						<b> Endereco:</b><br>
						<h:inputText size="70" id="pjcontratante_endereco_rua" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.logradouro}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					<td>
						<b> Número:</b><br>
						<h:inputText id="pjcontratante_endereco_numero" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.numero}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
			
			
				<tr>
					<td colspan="4">
						<b> Complemento:</b><br>
						<h:inputText size="110" id="pjcontratante_endereco_complemento" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.complemento}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
				
				
				<tr>
					<td colspan="3">
						<b> Bairro:</b><br>
						<h:inputText size="70" id="pjcontratante_endereco_bairro" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.bairro}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					<td>
						<b> CEP:</b><br>
						<h:inputText size="12" id="pjcontratante_endereco_cep" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.cep}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
				
				<tr>
					<td colspan="3">
						<b> Cidade:</b><br>
						<h:inputText size="70" id="pjcontratante_endereco_cidade" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.municipio.nome}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					<td>
						<b> UF:</b><br>
						<h:inputText size="3" id="pjcontratante_endereco_uf" value="#{prestacaoServico.obj.pessoaJuridicaContratante.endereco.municipio.unidadeFederativa.sigla}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
				
				
				<tr>
					<td colspan="4">
						<b> Telefone:</b><br>
						<h:inputText size="20" id="pjcontratante_telefone" value="#{prestacaoServico.obj.pessoaJuridicaContratante.pessoa.telefone}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
		
		
				<tr>
					<td colspan="3">
						<b> Nome do Dirigente:</b><br>
						<h:inputText size="50" id="pjcontratante_nomeDirigente" value="#{prestacaoServico.obj.pessoaJuridicaContratante.nomeDirigente}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
					<td>
						<b> Cargo do Dirigente:</b><br>
						<h:inputText size="40" id="pjcontratante_cargoDirigente" value="#{prestacaoServico.obj.pessoaJuridicaContratante.cargoDirigente}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
		
	
	
				<tr>
					<td colspan="2">
						<b> Esfera Administrativa:</b><br>
						<h:selectOneMenu id="pjcontratante_esferaAdm" value="#{prestacaoServico.obj.pessoaJuridicaContratante.tipoEsferaAdministrativa.id}" readonly="#{prestacaoServico.readOnly}">
						  <f:selectItems  value="#{prestacaoServico.allTipoEsferaAdministrativaCombo}" />
						</h:selectOneMenu> 						
					</td>
					
					<td colspan="2">
						<b> Outra Esfera Administrativa:</b><br>
						<h:inputText size="40" id="pjcontratante_outraEsferaAdm" value="#{prestacaoServico.obj.pessoaJuridicaContratante.esferaAdministrativaOutra}" readonly="#{prestacaoServico.readOnly}"/>
					</td>
				</tr>
				
			</table>


	  </td>
	</tr>
	
	<tfoot>
		<tr> <td colspan="4">
			<h:commandButton value="Cancelar" action="#{prestacaoServico.cancelar}" />
			<h:commandButton value="Avançar >>" action="#{prestacaoServico.irTelaEquipe}" />
		</td> </tr>
	</tfoot>
	

</table>

	
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>