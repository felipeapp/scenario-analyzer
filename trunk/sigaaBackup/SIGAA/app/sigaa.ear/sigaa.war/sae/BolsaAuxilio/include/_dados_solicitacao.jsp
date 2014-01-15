				<c:if test="${ bolsaAuxilioMBean.obj.adesaoCadUnico != null}">
					<tr>
						<td colspan="4">
							<%@include file="/geral/questionario/_respostas.jsp" %>
						</td>
					</tr>
				
					<tr>
						<td colspan="4">
							<t:newspaperTable value="#{bolsaAuxilioMBean.obj.adesaoCadUnico.listaConfortoFamiliar}" var="confortoFamiliar" newspaperColumns="2" 
										newspaperOrientation="horizontal" rowClasses="linhaPar, linhaImpar"  width="100%">					
						
								<t:column style="text-align: right;">
		                      		<h:outputText value="#{confortoFamiliar.item.item}" />
			                    </t:column>
			                    
			                    <t:column>
									<h:selectOneMenu value="#{confortoFamiliar.quantidade}" disabled="true" id="confortoFamiliar">
										<f:selectItem itemValue="0" itemLabel="Nenhum" />
										<f:selectItem itemValue="1" itemLabel="1" />
										<f:selectItem itemValue="2" itemLabel="2" />
										<f:selectItem itemValue="3" itemLabel="3" />
									</h:selectOneMenu>
			                    </t:column>
			                </t:newspaperTable>
						</td>				
					</tr>	
				</c:if>
				
				<tr>
					<th>
						<b>MATRÍCULA:</b> 	
					</th>
					<td>
						<h:outputText value="#{bolsaAuxilioMBean.obj.discente.matricula}" id="matricula" />
					</td>
				</tr>
				
                <tr>
					<th>
						<b>DISCENTE:</b> 	
					</th>
					<td>
						<h:outputText value="#{bolsaAuxilioMBean.obj.discente.nome}" id="discente" />
					</td>
				</tr>
				
				<tr>
					<th>
						<b>CURSO:</b> 
					</th>
					<td>
						<h:outputText value="#{bolsaAuxilioMBean.obj.discente.curso.descricao}" id="curso"/>
					</td>
				</tr>
				
				<tr>
					<th>
						<b>CEP:</b>
					</th>
					<td>
						<h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.cep}" id="cep"/>
					</td>
				</tr>
				
				<tr>
					<th>
						<b>BAIRRO:</b> 
					</th>
					<td>
						<h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.bairro}" id="bairro"/>
					</td>
				</tr>
				
				<tr>
					<th>
						<b>RUA:</b> 
					</th>
					<td>
						<h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.logradouro}" id="rua"/>
					</td>
				</tr>
				
				<tr>
					<th>
						<b>NÚMERO:</b> 
					</th>
					<td>
						<h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.numero}" id="numero"/>
					</td>
				</tr>
				
				<tr>
					<th>
						<b>CIDADE:</b> 
					</th>
					<td>
						<h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.municipio.nome}" id="cidade"/>
					</td>
				</tr>
				
				<tr>
					<th>
						<b>UF:</b> 
					</th>
					<td>
						<h:outputText value="#{bolsaAuxilioMBean.obj.discente.pessoa.enderecoContato.unidadeFederativa.sigla}" id="uf" />
					</td>
				</tr>
				
				<tr>
					<th>
						<b> TIPO DA BOLSA AUXÍLIO: </b>
					</th>
					<td>
						<h:outputText value="#{ bolsaAuxilioMBean.obj.tipoBolsaAuxilio.denominacao }" />
					</td>
				</tr>