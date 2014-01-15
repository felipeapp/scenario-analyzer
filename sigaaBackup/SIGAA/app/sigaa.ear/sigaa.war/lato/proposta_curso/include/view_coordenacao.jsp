		 <caption>Dados da Coordenação</caption>
		  <tr>
			 <th width="30%">Coordenador:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.coordenador.servidor.pessoa.nome}"/></td> 
			</tr>
			<tr>
	 		 <th>Email Contato:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.coordenador.emailContato}"/></td>
			</tr>
			<tr>
			 <th>Telefone Contato:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.coordenador.telefoneContato1}"/></td>
			</tr>
			<tr>
			 <th>Data Inicio Mandato:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.coordenador.dataInicioMandato}"/></td>
			</tr>
			<tr>
			 <th>Data Fim Mandato:</th>
			 <td><h:outputText value="#{cursoLatoMBean.obj.coordenador.dataFimMandato}"/></td>
			</tr>
			<tr>	
				<td colspan="4">
					<table class="subFormulario" width="100%">
					 <caption>Dados Básicos do Vice-Coordenador</caption>
						<tr>
						 <th width="30%">Vice-Coordenador:</th>
						 <td><h:outputText value="#{cursoLatoMBean.obj.viceCoordenador.servidor.pessoa.nome}"/></td>
						</tr>
						<tr>
						 <th>Email Contato:</th>
						 <td><h:outputText value="#{cursoLatoMBean.obj.viceCoordenador.emailContato}"/></td>
						</tr>
						<tr>
						 <th>Telefone Contato:</th>
						 <td><h:outputText value="#{cursoLatoMBean.obj.viceCoordenador.telefoneContato1}"/></td>
						</tr>
						<tr>
						 <th>Data Inicio Mandato:</th>
						 <td><h:outputText value="#{cursoLatoMBean.obj.viceCoordenador.dataInicioMandato}"/></td>
						</tr>
						<tr>
						 <th>Data Fim Mandato:</th>
						 <td><h:outputText value="#{cursoLatoMBean.obj.viceCoordenador.dataFimMandato}"/></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<table class="subFormulario" width="100%">
					  <caption>Secretário do Curso</caption>	
						<tr>
							<th width="30%">Secretário(a): </th>
							<td><h:outputText value="#{cursoLatoMBean.obj.secretario.usuario.nome}"/></td>
						</tr>
					</table> 
				</td>
			</tr>