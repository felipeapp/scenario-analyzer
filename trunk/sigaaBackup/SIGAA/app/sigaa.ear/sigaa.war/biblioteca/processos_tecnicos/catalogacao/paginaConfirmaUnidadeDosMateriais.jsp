<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>	
	
	<h2>  <ufrn:subSistema /> &gt; Cadastro de Materiais  &gt; Confirmação Incluisão Exemplares em Outras Unidades </h2>
	

	<div class="descricaoOperacao"> 
	
		 <p> Caro usuário, alguns materiais selecionados foram tombados para uma unidade diferente da unidade da sua biblioteca. 
		 Isso pode significar que, provavelmente, se estar incluindo materiais na biblioteca errada.</p> 
	
		 <p> Por favor, confirme se os materiais abaixo pertencem mesmo a sua biblioteca.</p> 
	
	</div>


	<h:form id="fromConfirmaUnidadesMateriais">
		
		<a4j:keepAlive beanName="materialInformacionalMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
		
		<%-- Para manter os dados quando é importado vários título e incluído materiais --%>
		<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

		<table class="listagem" id="tabelaDadosUnidadesIncluidas" style=" width: 80%" >
			
			<caption> Exemplares tombados para unidades diferentes</caption>
			
			<tr>
				<td>
					<table class="subFormulario" style="width: 100%">
						<caption> Unidades com permissão de incluir materiais: </caption>
						
						<tbody>
			
						<c:forEach items="#{materialInformacionalMBean.mensagemUnidadesPermisaoUsuario}" var="mensagemUnidadesPermissao" varStatus="loop">
							<tr>
								<td style="text-align: center">
									${mensagemUnidadesPermissao}
								</td>
							</tr>
						
						</c:forEach>
						
					</tbody>
						
					</table>
				</td>
			</tr>
			
			<tr>
				<td>
					<table class="subFormulario" style="width: 100%">
						
						<caption> Unidades dos tombamentos dos materiais: </caption>
					
						<thead>
							<tr>
								<th style="width: 40%">Material</th>
								<th>Unidade do Tombamento</th>
							</tr>
						</thead>
					
						<tbody>
							<c:forEach items="#{materialInformacionalMBean.dadosBensSemPermisaoUnidade}" var="dadoMaterialUnidadeDiferente" varStatus="loop">
								<tr  class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									<td>
										${dadoMaterialUnidadeDiferente.codigoBarras}
									</td>
									<td>
										${dadoMaterialUnidadeDiferente.descricaoUnidade}
									</td>
								</tr>
							
							</c:forEach>
							
						</tbody>
					
					</table>
				</td>
			</tr>
			
			
			<tfoot>
				<tr>
					<td colspan="3" style="width: 100%;  text-align: center; ">
				
						<h:commandButton id="cmdConfirmaInclusaoExemplar" value="Confirmar Inclusão do Material" action="#{materialInformacionalMBean.confirmaCadastroExemplar}" />		
							
						<h:commandButton value="<< Voltar" action="#{materialInformacionalMBean.telaIncluirNovoMaterial}" />
					
						<h:commandButton value="Cancelar" action="#{materialInformacionalMBean.cancelar}" immediate="true" id="cancelar"  onclick="#{confirm}"/>
						
					</td>
								
				</tr>
							
			</tfoot>
			
		</table>


	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>